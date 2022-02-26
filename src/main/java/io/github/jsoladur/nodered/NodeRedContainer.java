package io.github.jsoladur.nodered;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jsoladur.nodered.internal.InternalSettings;
import io.github.jsoladur.nodered.settings.Settings;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.modelmapper.ModelMapper;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * @author José María Sola Durán, https://github.com/jsoladur, @jsoladur
 */
public class NodeRedContainer extends GenericContainer<NodeRedContainer> {

    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME = "nodered/node-red";
    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION = "latest";
    private static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse(DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME + ":" + DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION);
    private static final int DEFAULT_HTTP_EXPOSED_PORT = 1880;
    private static final List<Integer> ALL_EXPOSED_PORTS = List.of(DEFAULT_HTTP_EXPOSED_PORT);
    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofMinutes(1);
    private static final String FLOWS_JSON_FILE_NAME = "flows.json";
    private static final String FLOWS_CRED_JSON_FILE_NAME = "flows_cred.json";
    //TODO: Think about how code this feature (Nashorn¿?)...
    private static final String SETTINGS_JS_FILE_NAME = "settings.js";

    private static final class Env {
        private static final String NODE_RED_CREDENTIAL_SECRET = "NODE_RED_CREDENTIAL_SECRET";
        private static final String NODE_OPTIONS = "NODE_OPTIONS";
    }

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private String flowsJson;
    private String flowsCredJson;

    private String settingsJs;
    private Settings settings;
    private boolean prettyPrintSettings;

    private String nodeRedCredentialSecret;
    private String nodeOptions;
    private Duration startupTimeout = DEFAULT_STARTUP_TIMEOUT;

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
        prettyPrintSettings = true;
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
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
        setWaitStrategy(Wait
                .forHealthcheck()
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
        if (this.hasFlowsJson()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.flowsJson)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + FLOWS_JSON_FILE_NAME);
            }
        }
        if (this.hasFlowsCredJson()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.flowsCredJson)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + FLOWS_CRED_JSON_FILE_NAME);
            }
        }
        if (this.hasSettingsJs()) {
            try (final var is = this.getClass().getClassLoader().getResourceAsStream(this.settingsJs)) {
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + SETTINGS_JS_FILE_NAME);
            }
        } else if (this.hasSettings()) {
            final var internalSettings = modelMapper.map(settings, InternalSettings.class);
            final String internalSettingsAsString = prettyPrintSettings ?
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(internalSettings) :
                    objectMapper.writeValueAsString(internalSettings);
            final String moduleExportsSettingsFile = String.format("module.exports = %1$2s", internalSettingsAsString);
            copyFileToContainer(Transferable.of(moduleExportsSettingsFile.getBytes(StandardCharsets.UTF_8)), "/data/" + SETTINGS_JS_FILE_NAME);
        }
    }

    private void printLoggerWarnDisableFeature() {
        logger().warn("This feature is disabled in " + this.getClass().getName());
    }
}
