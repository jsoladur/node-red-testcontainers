package io.github.jsoladur.nodered.utils;

import lombok.experimental.UtilityClass;
import org.testcontainers.shaded.okhttp3.MediaType;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

@UtilityClass
public class NodeRedConstants {

    public static final String DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME = "nodered/node-red";
    public static final String DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION = "latest";
    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse(DEFAULT_NODE_RED_DOCKER_IMAGE_BASE_NAME + ":" + DEFAULT_NODE_RED_DOCKER_IMAGE_VERSION);
    public static final int DEFAULT_HTTP_EXPOSED_PORT = 1880;
    public static final List<Integer> ALL_EXPOSED_PORTS = List.of(DEFAULT_HTTP_EXPOSED_PORT);
    public static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofMinutes(1);
    public static final String FLOWS_JSON_FILE_NAME = "flows.json";
    public static final String FLOWS_CRED_JSON_FILE_NAME = "flows_cred.json";
    public static final String SETTINGS_JS_FILE_NAME = "settings.js";

    public static final String NODE_RED_CATALOGUE_URL = "https://catalogue.nodered.org/catalogue.json";

    @UtilityClass
    public class Env {
        public static final String NODE_RED_CREDENTIAL_SECRET = "NODE_RED_CREDENTIAL_SECRET";
        public static final String NODE_OPTIONS = "NODE_OPTIONS";
    }

    @UtilityClass
    public class HttpClient {
        public static final MediaType APPLICATION_JSON_UTF8 = MediaType.parse("application/json; charset=utf-8");
    }
}
