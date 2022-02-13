package io.github.jsoladur.nodered;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * @author José María Sola Durán, https://github.com/jsoladur, @jsoladur
 */
public class NodeRedContainer extends GenericContainer<NodeRedContainer> {

    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME = "nodered/node-red";
    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION = "latest";
    private static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse(DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME + ":" + DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION);
    private static final int DEFAULT_HTTP_EXPOSED_PORT = 1880;
    private static final List<Integer> ALL_EXPOSED_PORTS = Arrays.asList(DEFAULT_HTTP_EXPOSED_PORT);
    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofMinutes(1);
    private static final String CUSTOM_FLOWS_JSON_FILE_NAME = "node-red-testcontainers_flows.json";

    private static final class Env {
        private static final String NODE_RED_CREDENTIAL_SECRET = "NODE_RED_CREDENTIAL_SECRET";
        private static final String FLOWS = "FLOWS";
        private static final String NODE_OPTIONS = "NODE_OPTIONS";
        private static final String NODE_RED_DISABLE_EDITOR = "NODE_RED_DISABLE_EDITOR";
    }

    private String flowsJson;
    private boolean disableEditor;
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
        withExposedPorts(ALL_EXPOSED_PORTS.stream().toArray(Integer[]::new));
        withLogConsumer(new Slf4jLogConsumer(logger()));
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
     *
     * @see <a href="https://nodered.org/docs/getting-started/docker">Running NODE-RED under Docker</a>
     * @param disableEditor NODE_RED_DISABLE_EDITOR env variable
     * @return self container
     * @since 0.1.0
     */
    public NodeRedContainer withDisableEditor(boolean disableEditor) {
        this.disableEditor = disableEditor;
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

    @Override
    @SneakyThrows
    protected void configure() {
        setWaitStrategy(Wait
                .forHttp("/")
                .forPort(DEFAULT_HTTP_EXPOSED_PORT)
                .withStartupTimeout(startupTimeout)
        );
        withEnv(Env.NODE_RED_DISABLE_EDITOR, String.valueOf(this.disableEditor));
        if (this.hasFlowsJson()) {
            withEnv(Env.FLOWS, CUSTOM_FLOWS_JSON_FILE_NAME);
        }
        if (this.nodeRedCredentialSecret != null && !this.nodeRedCredentialSecret.isBlank()) {
            withEnv(Env.NODE_RED_CREDENTIAL_SECRET, this.nodeRedCredentialSecret);
        }
        if (this.nodeOptions != null && !this.nodeOptions.isBlank()) {
            withEnv(Env.NODE_OPTIONS, this.nodeOptions);
        }
        // FIXME: To be implemented!
    }

    @Override
    @SneakyThrows
    protected void containerIsCreated(String containerId) {
        if (this.hasFlowsJson()) {
            try(final var is = this.getClass().getClassLoader().getResourceAsStream(this.flowsJson)){
                copyFileToContainer(Transferable.of(IOUtils.toByteArray(is)), "/data/" + CUSTOM_FLOWS_JSON_FILE_NAME);
            }
        }
        logger().debug("Finish copy flows.json");
    }

    private void printLoggerWarnDisableFeature() {
        logger().warn("This feature is disabled in " + this.getClass().getName());
    }
}
