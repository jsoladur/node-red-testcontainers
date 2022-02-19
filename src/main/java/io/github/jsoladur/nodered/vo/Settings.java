package io.github.jsoladur.nodered.vo;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class Settings {

    /**
     * <p>The file containing the flows. If not set, defaults to flows_<hostname>.json</p>
     */
    @Builder.Default
    private String flowFile = "flows.json";
    /**
     * <p>By default, the flow JSON will be formatted over multiple lines making
     * it easier to compare changes when using version control.
     * To disable pretty-printing of the JSON set the following property to false.</p>
     */
    @Builder.Default
    private boolean flowFilePretty = true;
    /**
     * <p>The maximum size of HTTP request that will be accepted by the runtime api</p>
     */
    @Builder.Default
    private String apiMaxLength = "5mb";
    /**
     * <p>The following property can be used to configure cross-origin resource sharing
     *  in the HTTP nodes. See <a href="https://github.com/troygoode/node-cors#configuration-options">https://github.com/troygoode/node-cors#configuration-options</a>
     *  for details on its contents. The following is a basic permissive set of options:</p>
     */
    private HttpNodeCors httpNodeCors;
    /**
     * <p>Run node-red in your preferred language.
     *    Available languages include: en-US (default), ja, de, zh-CN, zh-TW, ru, ko
     *    Some languages are more complete than others.</p>
     */
    private String lang;

    /**
     * <p>Configure the logging output</p>
     * <p>Only console logging is currently supported</p>
     */
    @Builder.Default
    private Logging logging = Logging.builder().build();
    /**
     * <p>`global.keys()` returns a list of all properties set in global context.
     *      This allows them to be displayed in the Context Sidebar within the editor.
     *      In some circumstances it is not desirable to expose them to the editor. The
     *      following property can be used to hide any property set in `functionGlobalContext`
     *      from being list by `global.keys()`.
     *      By default, the property is set to false to avoid accidental exposure of
     *      their values. Setting this to true will cause the keys to be listed.</p>
     */
    private String exportGlobalContextKeys;
    /**
     * <p>Configure how the runtime will handle external npm modules.
     *      This covers:
     *      - whether the editor will allow new node modules to be installed
     *      - whether nodes, such as the Function node are allowed to have their
     *      own dynamically configured dependencies.
     *      The allow/denyList options can be used to limit what modules the runtime
     *      will install/load. It can use '*' as a wildcard that matches anything.</p>
     */
    @Builder.Default
    private ExternalModules externalModules = ExternalModules.builder().build();
    /**
     * <p>The following property can be used to disable the editor. The admin API
     *      is not affected by this option. To disable both the editor and the admin
     *      API, use either the httpRoot or httpAdminRoot properties</p>
     */
    private boolean disableEditor;

    /**
     * <p>Allow the Function node to load additional npm modules directly</p>
     */
    @Builder.Default
    private boolean functionExternalModules = true;
    /**
     * <p></p>
     */
    private Integer nodeMessageBufferMaxLength;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long execMaxBufferSize = 10000000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long httpRequestTimeout = 120000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long mqttReconnectTime = 15000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long serialReconnectTime = 15000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long socketReconnectTime = 10000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long socketTimeout = 120000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long tcpMsgQueueSize = 2000L;
    /**
     * <p></p>
     */
    @Builder.Default
    private Long inboundWebSocketTimeout = 5000L;
    /**
     * <p>To disable the option for using local files for storing keys and
     *    certificates in the TLS configuration node, set this to true.</p>
     */
    @Builder.Default
    private boolean tlsConfigDisableLocalFiles = true;

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class HttpNodeCors {
        @Builder.Default
        private String origin = "*";
        @Builder.Default
        private String methods = "GET,PUT,POST,DELETE";
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Logging {

        @Builder.Default
        private Console console = Console.builder().build();

        @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
        public static class Console {
            @Builder.Default
            private String level = "info";
            private boolean metrics;
            private boolean audit;
        }
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class ExternalModules {
        private boolean autoInstall;
        private Integer autoInstallRetry;
        @Builder.Default
        private Palette palette = Palette.builder().build();
        @Builder.Default
        private Modules modules = Modules.builder().build();

        @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
        public static class Palette {
            /**
             * <p></p>
             */
            @Builder.Default
            private boolean allowInstall = true;
            /**
             * <p></p>
             */
            @Builder.Default
            private boolean allowUpdate = true;
            /**
             * <p></p>
             */
            @Builder.Default
            private boolean allowUpload = true;
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> allowList = List.of("*");
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> denyList = List.of();
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> allowUpdateList = List.of("*");
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> denyUpdateList = List.of();


        }

        @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
        public static class Modules {
            /**
             * <p></p>
             */
            @Builder.Default
            private boolean allowInstall = true;
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> allowList = List.of("*");
            /**
             * <p></p>
             */
            @Builder.Default
            private List<String> denyList = List.of();
        }
    }
}
