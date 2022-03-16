package io.github.jsoladur.nodered.internal.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class NodeRedCatalogue {

    private List<Module> modules;

    @Getter @Setter
    public static class Module {
        private String id;
        private String version;
        private List<String> types;
        private String url;
    }
}
