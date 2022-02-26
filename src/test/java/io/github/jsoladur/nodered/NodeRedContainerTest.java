package io.github.jsoladur.nodered;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jsoladur.nodered.helpers.dtos.Posts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@Testcontainers
class NodeRedContainerTest {

    @Container
    static final NodeRedContainer nodeRedContainer =
            new NodeRedContainer()
                    .withFlowsJson("flows_jsonplaceholder_posts.json")
                    .withNodeOptions("--max-old-space-size=1024");

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
                .url(nodeRedContainer.getNodeRedUrl() + "/posts")
                .build();
        final var call = client.newCall(request);
        final var response = call.execute();
        final var postList = objectMapper.readValue(response.body().bytes(), new TypeReference<List<Posts>>() {});
        assertFalse(postList.isEmpty());
    }
}
