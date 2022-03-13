package io.github.jsoladur.nodered.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter @Builder
public class ThirdPartyLibraryNodesDependency {

    private String scope;
    @NonNull
    private String name;
    @Builder.Default
    private String version = "latest";

    public String getFullName() {
        final String fullName;
        if (scope != null && !scope.isBlank()) {
            fullName = String.format("%1$2s/%2$2s:%3$2s", this.scope, this.name, this.version);
        } else {
            fullName = String.format("%1$2s:%2$2s", this.name, this.version);
        }
        return fullName;
    }
}
