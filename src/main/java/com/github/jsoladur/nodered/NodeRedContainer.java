package com.github.jsoladur.nodered;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

public class NodeRedContainer extends GenericContainer<NodeRedContainer> {

    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME = "nodered/node-red";
    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION = "2.2.0";
    private static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse(DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME + ":" + DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION);
    private static final int DEFAULT_HTTP_EXPOSED_PORT = 1880;
    private static final List<Integer> ALL_EXPOSED_PORTS = List.of(DEFAULT_HTTP_EXPOSED_PORT);
    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofMinutes(1);

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

    public NodeRedContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    public NodeRedContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        if (!dockerImageName.isCompatibleWith(DEFAULT_DOCKER_IMAGE_NAME)) {
            throw new IllegalArgumentException(String.format("%1$s isn't compatible with %2$s", dockerImageName.asCanonicalNameString(),
                    DEFAULT_DOCKER_IMAGE_NAME.asCanonicalNameString()));
        }
        withExposedPorts(ALL_EXPOSED_PORTS.toArray(Integer[]::new));
        // FIXME
    }

    public NodeRedContainer withFlowsJson(String flowsJson) {
        this.flowsJson = flowsJson;
        return self();
    }

    public NodeRedContainer withDisableEditor(boolean disableEditor) {
        this.disableEditor = disableEditor;
        return self();
    }

    public NodeRedContainer withNodeRedCredentialSecret(String nodeRedCredentialSecret) {
        this.nodeRedCredentialSecret = nodeRedCredentialSecret;
        return self();
    }

    public NodeRedContainer withNodeOptions(String nodeOptions) {
        this.nodeOptions = nodeOptions;
        return self();
    }

    public NodeRedContainer withStartupTimeout(Duration startupTimeout) {
        this.startupTimeout = startupTimeout;
        return self();
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

    @Override
    protected void configure() {
        super.configure();
        setWaitStrategy(Wait
                .forHttp("/")
                .forPort(DEFAULT_HTTP_EXPOSED_PORT)
                .withStartupTimeout(startupTimeout)
        );
        withEnv(Env.NODE_RED_DISABLE_EDITOR, String.valueOf(this.disableEditor));
        if (this.flowsJson != null && !this.flowsJson.isBlank()) {
            // FIXME: To be implemented!
        }
        if (this.nodeRedCredentialSecret != null && !this.nodeRedCredentialSecret.isBlank()) {
            withEnv(Env.NODE_RED_CREDENTIAL_SECRET, this.nodeRedCredentialSecret);
        }
        if (this.nodeOptions != null && !this.nodeOptions.isBlank()) {
            withEnv(Env.NODE_OPTIONS, this.nodeOptions);
        }
        // FIXME: To be implemented!
    }

    private void printLoggerWarnDisableFeature() {
        logger().warn("This feature is disabled in " + this.getClass().getName());
    }


}
