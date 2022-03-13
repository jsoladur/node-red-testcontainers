package io.github.jsoladur.nodered;

import com.github.dockerjava.api.command.CreateContainerCmd;
import io.github.jsoladur.nodered.helpers.dtos.MariaDBVersion;
import io.github.jsoladur.nodered.vo.Settings;
import io.github.jsoladur.nodered.vo.ThirdPartyLibraryNodesDependency;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Testcontainers
class NodeRedContainerUserCredentialTest {

    static final String MARIA_DB_CONTAINER_NAME = "mariadb-8524a3c4-0e6e-419d-9f3e-f2dab71f40f4";
    static final Network network = Network.newNetwork();

    @Container
    static final MariaDBContainer mariaDbContainer =
            (MariaDBContainer) new MariaDBContainer(DockerImageName.parse("mariadb"))
                    .withPassword("my_cool_secret")
                    .withNetwork(network)
                    .withCreateContainerCmdModifier(cmd -> {
                        CreateContainerCmd.class.cast(cmd).withName(MARIA_DB_CONTAINER_NAME);
                    });
    @Container
    static final NodeRedContainer nodeRedContainer =
            new NodeRedContainer()
                    .withThirdPartyLibraryNodesDependencies(
                            ThirdPartyLibraryNodesDependency
                                    .builder()
                                    .name("node-red-node-mysql")
                                    .version("1.0.1")
                                    .build()
                    )
                    .withSettings(Settings
                            .builder()
                            .externalModules(Settings.ExternalModules
                                    .builder()
                                    .autoInstall(true)
                                    .build())
                            .credentialSecret("c49f73806be83ebe2f10dadec4bdcfce42e3964b4b58ce36d01551e0c51926ea")
                            .disableEditor(false)
                            .build())
                    .withFlowsJson("mariadb/flows.json")
                    .withFlowsCredJson("mariadb/flows_cred.json")
                    .withNetwork(network);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll() {
        log.info("Network used = {}", network.getId());
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
