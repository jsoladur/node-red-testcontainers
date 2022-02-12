package com.github.jsoladur.nodered;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsoladur.nodered.helpers.dtos.Posts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Testcontainers
class NodeRedContainerTest {

    @Container
    static final NodeRedContainer nodeRedContainer = new NodeRedContainer().withFlowsJson("flows_jsonplaceholder_posts.json");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterAll
    static void afterAll() {
        nodeRedContainer.close();
    }

    @Test
    @SneakyThrows
    void test() {
        log.info("NODE-RED url = {}", nodeRedContainer.getNodeRedUrl());
        final var request = HttpRequest.newBuilder(URI.create(nodeRedContainer.getNodeRedUrl() + "/posts")).GET().build();
        final var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
        final var postList = objectMapper.readValue(response.body(), new TypeReference<List<Posts>>() {});
        Assertions.assertFalse(postList.isEmpty());
    }
}
