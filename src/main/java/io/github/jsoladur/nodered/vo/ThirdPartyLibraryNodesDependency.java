package io.github.jsoladur.nodered.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter @Builder
public class ThirdPartyLibraryNodesDependency {

    @NonNull
    private String module;
    @Builder.Default
    private String version = "latest";
}
