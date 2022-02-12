package com.github.jsoladur.nodered;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class NodeRedContainer extends GenericContainer<NodeRedContainer> {

    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME = "nodered/node-red";
    private static final String DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION = "2.2.0";
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

    public NodeRedContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    public NodeRedContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        if (!dockerImageName.isCompatibleWith(DEFAULT_DOCKER_IMAGE_NAME)) {
            throw new IllegalArgumentException(String.format("%1$s isn't compatible with %2$s", dockerImageName.asCanonicalNameString(),
                    DEFAULT_DOCKER_IMAGE_NAME.asCanonicalNameString()));
        }
        withExposedPorts(ALL_EXPOSED_PORTS.stream().toArray(Integer[]::new));
        // FIXME
    }

    public NodeRedContainer withFlowsJson(String flowsJson) {
        this.flowsJson = flowsJson;
        return self();
    }

    public boolean hasFlowsJson() {
        return this.flowsJson != null && !this.flowsJson.isBlank();
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
