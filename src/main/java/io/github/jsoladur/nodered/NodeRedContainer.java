package io.github.jsoladur.nodered;

import com.github.dockerjava.api.command.InspectContainerResponse;
import io.github.jsoladur.nodered.internal.helpers.NodeRedRestApiClient;
import io.github.jsoladur.nodered.internal.vo.InternalSettings;
import io.github.jsoladur.nodered.vo.Settings;
import io.github.jsoladur.nodered.vo.ThirdPartyLibraryNodesDependency;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.modelmapper.ModelMapper;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.org.apache.commons.lang.ObjectUtils;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static io.github.jsoladur.nodered.utils.NodeRedConstants.*;
import static java.util.stream.Collectors.*;

/**
 * @author José María Sola Durán, https://github.com/jsoladur, @jsoladur
 */
public class NodeRedContainer extends GenericContainer<NodeRedContainer> {

    private String flowsJson;
    private String flowsCredJson;
    private String settingsJs;
    private Settings settings;
    private boolean prettyPrintSettings;
    private Set<ThirdPartyLibraryNodesDependency> thirdPartyLibraryNodesDependencies = Collections.unmodifiableSet(Collections.emptySet());
    private boolean validateThirdPartyLibraryNodesDependencies;
    private String nodeRedCredentialSecret;
    private String nodeOptions;
    private Duration startupTimeout = DEFAULT_STARTUP_TIMEOUT;

    private final OkHttpClient okHttpClient;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final NodeRedRestApiClient nodeRedRestApiClient;

    /**
     * <p>Create NodeRedContainer with <a href="https://hub.docker.com/r/nodered/node-red/">nodered/node-red:latest</a> docker image</p>
     * @since 0.1.0
     */
    public NodeRedContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    /**
     * <p>Create a NodeRedContainer by passing the full docker image name</p>
     * @param dockerImageName Full docker image name, e.g. nodered/node-red:2.2.0
     * @since 0.1.0
     */
    public NodeRedContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        if (!dockerImageName.isCompatibleWith(DEFAULT_DOCKER_IMAGE_NAME)) {
            throw new IllegalArgumentException(String.format("%1$s isn't compatible with %2$s", dockerImageName.asCanonicalNameString(),
                    DEFAULT_DOCKER_IMAGE_NAME.asCanonicalNameString()));
        }
        withExposedPorts(ALL_EXPOSED_PORTS.toArray(Integer[]::new));
        withLogConsumer(new Slf4jLogConsumer(logger()));
        prettyPrintSettings = validateThirdPartyLibraryNodesDependencies = true;
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        okHttpClient = new OkHttpClient.Builder().build();
        nodeRedRestApiClient = new NodeRedRestApiClient(this, okHttpClient, objectMapper);
    }

    /**
     * flows configuration file to run in NODE-RED container instance
     * @param flowsJson flows configuration file
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @return self container
     * @since 0.1.0
     */
    public NodeRedContainer withFlowsJson(String flowsJson) {
        this.flowsJson = flowsJson;
        return self();
    }

    /**
     * flows crendentials configuration file (flows_cred.json) to run in NODE-RED container instance
     * @param flowsCred flows crendentials configuration file (flows_cred.json)
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withFlowsCredJson(String flowsCred) {
        this.flowsCredJson = flowsCred;
        return self();
    }

    /**
     * your settings file
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @param settingsJs settings.js file
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withSettingsJs(String settingsJs) {
        this.settingsJs = settingsJs;
        return self();
    }

    /**
     * your settings.js file as object representation
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @param settings settings.js file as object representation
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withSettings(Settings settings) {
        this.settings = settings;
        return self();
    }

    /**
     * Pretty print of settings.js file as object representation. 'true' is the default value
     * @param prettyPrintSettings pretty print of settings.js file as object representation
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withPrettyPrintSettings(boolean prettyPrintSettings) {
        this.prettyPrintSettings = prettyPrintSettings;
        return self();
    }

    /**
     *
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @param nodeRedCredentialSecret NODE_RED_CREDENTIAL_SECRET env variable
     * @return self container
     * @since 0.1.0
     */
    public NodeRedContainer withNodeRedCredentialSecret(String nodeRedCredentialSecret) {
        this.nodeRedCredentialSecret = nodeRedCredentialSecret;
        return self();
    }

    /**
     * List of third party libraries dependencies that flows needs in runtime
     * @param thirdPartyLibraryNodesDependencies
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withThirdPartyLibraryNodesDependencies(@NonNull ThirdPartyLibraryNodesDependency... thirdPartyLibraryNodesDependencies) {
        if (thirdPartyLibraryNodesDependencies.length <= 0) {
            throw new IllegalArgumentException("thirdPartyLibraryNodesDependencies must contains at least one 3rd party dependency");
        }
        this.thirdPartyLibraryNodesDependencies = Collections.unmodifiableSet(Set.of(thirdPartyLibraryNodesDependencies));
        final var repeatModules = this.thirdPartyLibraryNodesDependencies
                .stream()
                .collect(groupingBy(ThirdPartyLibraryNodesDependency::getModule, counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(toList());
        if (!repeatModules.isEmpty()) {
            throw new IllegalArgumentException(String
                    .format("thirdPartyLibraryNodesDependencies contains the next duplicates libraries: %1$2s", String.join(", ", repeatModules)));
        }
        return self();
    }

    /**
     * Enable/disable validation of third party library nodes dependencies before create and run NODE-RED container
     * @param validateThirdPartyLibraryNodesDependencies
     * @return self container
     * @since 0.2.0
     */
    public NodeRedContainer withValidateThirdPartyLibraryNodesDependencies(boolean validateThirdPartyLibraryNodesDependencies) {
        this.validateThirdPartyLibraryNodesDependencies = validateThirdPartyLibraryNodesDependencies;
        return self();
    }

    /**
     * <p>Set value for NODE_OPTIONS env variable</p>
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @param nodeOptions NODE_OPTIONS env variable
     * @return self container
     * @since 0.1.0
     */
    public NodeRedContainer withNodeOptions(String nodeOptions) {
        this.nodeOptions = nodeOptions;
        return self();
    }

    /**
     * <p>Set startup timeout to wait that the container start</p>
     * <p>By default, the value is 1 minute</p>
     * @param startupTimeout startup timeout
     * @return self container
     * @since 0.1.0
     */
    public NodeRedContainer withStartupTimeout(Duration startupTimeout) {
        this.startupTimeout = startupTimeout;
        return self();
    }

    /**
     * NODE-RED instance URL, e.g http://localhost:51134
     * @return Base URL to access to NODE-RED instance
     * @since 0.1.0
     */
    public String getNodeRedUrl() {
        return String.format("http://%1$2s:%2$2s", getContainerIpAddress(), getMappedPort(DEFAULT_HTTP_EXPOSED_PORT));
    }

    @Override
    public NodeRedContainer withNetwork(Network network) {
        return super.withNetwork(network);
    }

    @Override
    public NodeRedContainer withCommand(String cmd) {
        this.printLoggerWarnDisableFeature();
        return self();
    }

    @Override
    public NodeRedContainer withCommand(String... commandParts) {
        this.printLoggerWarnDisableFeature();
        return self();
    }

    /**
     * @return true if flows configuration file was set. Otherwise false
     */
    protected boolean hasFlowsJson() {
        return this.flowsJson != null && !this.flowsJson.isBlank();
    }

    /**
     * @return true if flows crendentials configuration file (flows_cred.json) file was set. Otherwise false
     */
    protected boolean hasFlowsCredJson() {
        return this.flowsCredJson != null && !this.flowsCredJson.isBlank();
    }

    /**
     * @return true if your settings file was set. Otherwise false
     */
    protected boolean hasSettingsJs() {
        return this.settingsJs != null && !this.settingsJs.isBlank();
    }

    /**
     * @return true Settings object property was set. Otherwise false
     */
    protected boolean hasSettings() {
        return this.settings != null;
    }

    @Override
    @SneakyThrows
    protected void configure() {
        validateThirdPartyLibraryNodesDependencies();
        setWaitStrategy(Wait
                .forHttp("/")
                .forPort(DEFAULT_HTTP_EXPOSED_PORT)
                .forStatusCodeMatching(code -> code >= 200 && code < 500)
                .withStartupTimeout(startupTimeout)
        );
        if (this.nodeRedCredentialSecret != null && !this.nodeRedCredentialSecret.isBlank()) {
            withEnv(Env.NODE_RED_CREDENTIAL_SECRET, this.nodeRedCredentialSecret);
        }
        if (this.nodeOptions != null && !this.nodeOptions.isBlank()) {
            withEnv(Env.NODE_OPTIONS, this.nodeOptions);
        }
    }

    @Override
    @SneakyThrows
    protected void containerIsCreated(String containerId) {
        if (this.hasSettingsJs() && this.hasSettings()) {
            logger().warn("settingsJs file property and settings object property, both was set. The settings object will be ignored!");
        }
        // flows.json
        if (this.hasFlowsJson()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.flowsJson)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + FLOWS_JSON_FILE_NAME);
            }
        }
        // flows_cred.json
        if (this.hasFlowsCredJson()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.flowsCredJson)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + FLOWS_CRED_JSON_FILE_NAME);
            }
        }
        // Inject settings.js into container
        if (this.hasSettingsJs()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.settingsJs)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + SETTINGS_JS_FILE_NAME);
            }
        } else if (this.hasSettings()) {
            final var internalSettings = modelMapper.map(ObjectUtils.defaultIfNull(settings, Settings.builder().build()), InternalSettings.class);
            final String internalSettingsAsString = prettyPrintSettings ?
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(internalSettings) :
                    objectMapper.writeValueAsString(internalSettings);
            final String moduleExportsSettingsFile = String.format("module.exports = %1$2s", internalSettingsAsString);
            copyFileToContainer(Transferable.of(moduleExportsSettingsFile.getBytes(StandardCharsets.UTF_8)), "/data/" + SETTINGS_JS_FILE_NAME);
        }
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo, boolean reused) {
        logger().debug("The NODE-RED container name is '{}'", containerInfo.getName());
        // XXX: Install third party dependencies...
        // @see https://github.com/node-red/node-red-admin/blob/master/lib/commands/install.js
        for (final var thirdPartyLibrary : thirdPartyLibraryNodesDependencies) {
            nodeRedRestApiClient.installThirdPartyLibraryNodesDependency(thirdPartyLibrary);
        }
    }

    private void validateThirdPartyLibraryNodesDependencies() {
        if (this.validateThirdPartyLibraryNodesDependencies) {
            // XXX: 1.) HTTP Request to https://catalogue.nodered.org/catalogue.json
            final var nodeRedCatalogueModules = nodeRedRestApiClient.getNodeRedCatalogueModules();
            // XXX: 2.) Validate 3rd party dependencies, comparing there one with catalogue
            for (final var thirdPartyLibrary : thirdPartyLibraryNodesDependencies) {
                if (nodeRedCatalogueModules.stream().noneMatch(catalogueModule -> catalogueModule.getId().equals(thirdPartyLibrary.getModule()))){
                    throw new IllegalStateException(String.format("%1$2s is a unknown library in NODE-RED Catalogue", thirdPartyLibrary.getModule()));
                }
            }
        }
    }

    private void printLoggerWarnDisableFeature() {
        logger().warn("This feature is disabled in " + this.getClass().getName());
    }
}
