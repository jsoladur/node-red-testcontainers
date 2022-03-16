package io.github.jsoladur.nodered.internal.helpers;

import io.github.jsoladur.nodered.NodeRedContainer;
import io.github.jsoladur.nodered.utils.NodeRedConstants;
import io.github.jsoladur.nodered.vo.ThirdPartyLibraryNodesDependency;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.shaded.okhttp3.RequestBody;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeRedRestApiClient {

    private static NodeRedRestApiClient INSTANCE;

    private final NodeRedContainer nodeRedContainer;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void installThirdPartyLibraryNodesDependency(ThirdPartyLibraryNodesDependency thirdPartyLibraryNodesDependency){
        final var request = new Request.Builder().url(nodeRedContainer.getNodeRedUrl() + "/nodes").
                post(RequestBody.create(NodeRedConstants.HttpClient.APPLICATION_JSON_UTF8, objectMapper.writeValueAsBytes(thirdPartyLibraryNodesDependency))).build();
        final var response = okHttpClient.newCall(request).execute();
        // FIXME: Improve handle response
        if (response.code() != 200) {
            throw new IllegalStateException(String.format("3rd party dependency %1$2s@%2$2s can't be installed", thirdPartyLibraryNodesDependency.getModule(), thirdPartyLibraryNodesDependency.getVersion()));
        }
    }

    public static final NodeRedRestApiClient newInstance(final NodeRedContainer nodeRedContainer, final OkHttpClient okHttpClient, final ObjectMapper objectMapper) {
        if (INSTANCE == null) {
            INSTANCE = new NodeRedRestApiClient(nodeRedContainer, okHttpClient, objectMapper);
        }
        return INSTANCE;
    }
}
