package io.github.jsoladur.nodered;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jsoladur.nodered.helpers.dtos.MariaDBVersion;
import io.github.jsoladur.nodered.settings.Settings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Testcontainers
class NodeRedContainerUserCredentialTest {

    @Container
    static final MariaDBContainer mariaDbContainer =
            new MariaDBContainer(DockerImageName.parse("mariadb"))
                    .withPassword("my_cool_secret");

    @Container
    static final NodeRedContainer nodeRedContainer =
            new NodeRedContainer()
                    .withSettings(Settings
                            .builder()
                            .externalModules(Settings.ExternalModules
                                    .builder()
                                    .autoInstall(true)
                                    .build())
                            .credentialSecret("3f01f2979636b984e84eb5eb4ace598a3cf3737eb1ce5249e6d2ed4b925eabd8")
                            .disableEditor(false)
                            .build())
                    .withFlowsJson("mariadb/flows.json");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() {
        log.info("NODE-RED url = {}", nodeRedContainer.getNodeRedUrl());
    }

    @AfterAll
    static void afterAll() {
        nodeRedContainer.close();
    }

    @Test
    @SneakyThrows
    void test() {
        final var client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(nodeRedContainer.getNodeRedUrl() + "/test")
                .build();
        final var call = client.newCall(request);
        final var response = call.execute();
        final var mariaDBVersion = objectMapper.readValue(response.body().bytes(), MariaDBVersion.class);
        assertNotNull(mariaDBVersion.getVersion());
    }
}
