package io.github.jsoladur.nodered.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data @Builder
public class ThirdPartyLibraryNodesDependency {

    @NonNull
    private String module;
    @Builder.Default
    private String version = "latest";
}
