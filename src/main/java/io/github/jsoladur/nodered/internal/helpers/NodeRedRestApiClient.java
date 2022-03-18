package io.github.jsoladur.nodered.internal.helpers;

import io.github.jsoladur.nodered.NodeRedContainer;
import io.github.jsoladur.nodered.internal.vo.ErrorResponse;
import io.github.jsoladur.nodered.internal.vo.NodeRedCatalogue;
import io.github.jsoladur.nodered.utils.NodeRedConstants;
import io.github.jsoladur.nodered.vo.ThirdPartyLibraryNodesDependency;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.shaded.okhttp3.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.github.jsoladur.nodered.utils.NodeRedConstants.NODE_RED_CATALOGUE_URL;

@RequiredArgsConstructor
public class NodeRedRestApiClient {

    private final NodeRedContainer nodeRedContainer;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void installThirdPartyLibraryNodesDependency(ThirdPartyLibraryNodesDependency thirdPartyLibraryNodesDependency){
        final var request = new Request.Builder().url(nodeRedContainer.getNodeRedUrl() + "/nodes").
                post(RequestBody.create(NodeRedConstants.HttpClient.APPLICATION_JSON_UTF8, objectMapper.writeValueAsBytes(thirdPartyLibraryNodesDependency))).build();
        final var response = okHttpClient.newCall(request).execute();
        final var responseCode = response.code();
        if (responseCode != 200) {
            String errorDescription = "unknown";
            if (responseCode == 400 && response.body() != null) {
                try {
                    final var errorResponse = objectMapper.readValue(response.body().bytes(), ErrorResponse.class);
                    if (errorResponse.getMessage() != null && !errorResponse.getMessage().isBlank()) {
                        errorDescription = errorResponse.getMessage();
                    }
                } catch (Exception e) {}
            }
            throw new IllegalStateException(String.format("3rd party dependency %1$2s@%2$2s can't be installed. Reason: %3$2s", thirdPartyLibraryNodesDependency.getModule(),
                    thirdPartyLibraryNodesDependency.getVersion(), errorDescription));
        }
    }

    @SneakyThrows
    public List<NodeRedCatalogue.Module> getNodeRedCatalogueModules() {
        final var request = new Request.Builder().get().url(NODE_RED_CATALOGUE_URL).build();
        final var responseBody = okHttpClient.newCall(request).execute().body();
        final var nodeRedCatalogue = objectMapper.readValue(responseBody.bytes(), NodeRedCatalogue.class);
        return Objects.nonNull(nodeRedCatalogue.getModules()) ? nodeRedCatalogue.getModules() : Collections.emptyList();
    }
}
