package io.github.jsoladur.nodered.settings;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
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
     * <p>TCP port that the Node-RED web server is listening on</p>
     */
    @Builder.Default
    private Integer uiPort = 1880;
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
     * <p>Customising the editor
     * See <a href="https://nodered.org/docs/user-guide/runtime/configuration#editor-themes">https://nodered.org/docs/user-guide/runtime/configuration#editor-themes</a>
     * for all available options.</p>
     */
    @Builder.Default
    private EditorTheme editorTheme = EditorTheme.builder().build();

    /**
     * <p>Allow the Function node to load additional npm modules directly</p>
     */
    @Builder.Default
    private boolean functionExternalModules = true;

    /**
     * <p>The maximum number of messages nodes will buffer internally as part of their
     *    operation. This applies across a range of nodes that operate on message sequences.
     *    defaults to no limit. A value of 0 also means no limit is applied.</p>
     */
    private Integer nodeMessageBufferMaxLength;
    /**
     * <p>The maximum length, in characters, of any message sent to the debug sidebar tab</p>
     */
    @Builder.Default
    private Integer debugMaxLength = 1000;
    /**
     * <p>Maximum buffer size for the exec node. Defaults to 10M</p>
     */
    @Builder.Default
    private Long execMaxBufferSize = 10000000L;
    /**
     * <p>Timeout in milliseconds for HTTP request connections. Defaults to 120s</p>
     */
    @Builder.Default
    private Long httpRequestTimeout = 120000L;
    /**
     * <p>Retry time in milliseconds for MQTT connections</p>
     */
    @Builder.Default
    private Long mqttReconnectTime = 15000L;
    /**
     * <p>Retry time in milliseconds for Serial port connections</p>
     */
    @Builder.Default
    private Long serialReconnectTime = 15000L;
    /**
     * <p>Retry time in milliseconds for TCP socket connection</p>
     */
    @Builder.Default
    private Long socketReconnectTime = 10000L;
    /**
     * <p>Timeout in milliseconds for TCP server socket connections. Defaults to no timeout</p>
     */
    private Long socketTimeout;
    /**
     * <p>Maximum number of messages to wait in queue while attempting to connect to TCP socket
     *    defaults to 1000</p>
     */
    @Builder.Default
    private Long tcpMsgQueueSize = 1000L;
    /**
     * <p>Timeout in milliseconds for inbound WebSocket connections that do not
     *    match any configured node. Defaults to 5000</p>
     */
    @Builder.Default
    private Long inboundWebSocketTimeout = 5000L;
    /**
     * <p>To disable the option for using local files for storing keys and
     *    certificates in the TLS configuration node, set this to true.</p>
     */
    @Builder.Default
    private boolean tlsConfigDisableLocalFiles = true;

    @Getter @Builder
    public static class HttpNodeCors {
        @Builder.Default
        private String origin = "*";
        @Builder.Default
        private String methods = "GET,PUT,POST,DELETE";
    }

    @Getter @Builder
    public static class Logging {

        @Builder.Default
        private Console console = Console.builder().build();

        @Getter @Builder
        public static class Console {
            @Builder.Default
            private String level = "info";
            private boolean metrics;
            private boolean audit;
        }
    }

    @Getter @Builder
    public static class ExternalModules {
        private boolean autoInstall;
        private Integer autoInstallRetry;
        @Builder.Default
        private Palette palette = Palette.builder().build();
        @Builder.Default
        private Modules modules = Modules.builder().build();

        @Getter @Builder
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

        @Getter @Builder
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

    @Getter @Builder
    public static class EditorTheme {
        private String theme;
        private boolean tours;
        @Builder.Default
        private Palette palette = Palette.builder().build();
        @Builder.Default
        /**
         * <p>To enable the Projects feature, set this value to true</p>
         */
        private Projects projects = Projects.builder().build();

        @Getter @Builder
        public static class Palette {
            /**
             * <p>The following property can be used to order the categories in the editor
             * palette. If a node's category is not in the list, the category will get
             * added to the end of the palette.
             * If not set, the following default order is used:
             * ['subflows', 'common', 'function', 'network', 'sequence', 'parser', 'storage']</p>
             */
            @Builder.Default
            private List<String> categories = List.of("subflows", "common", "function", "network", "sequence", "parser", "storage");
        }

        @Getter @Builder
        public static class Projects {
            private boolean enabled;
            @Builder.Default
            private Workflow workflow = Workflow.builder().build();
            @Builder.Default
            private CodeEditor codeEditor = CodeEditor.builder().build();

            @Getter @Builder
            public static class Workflow {
                /**
                 * <p>Set the default projects workflow mode.
                 *  - manual - you must manually commit changes
                 *  - auto - changes are automatically committed
                 * This can be overridden per-user from the 'Git config'
                 * section of 'User Settings' within the editor</p>
                 */
                @Builder.Default
                private String mode = "manual";

            }

            @Getter @Builder
            public static class CodeEditor {
                @Builder.Default
                private String lib = "ace";
                @Builder.Default
                private Options options = Options.builder().build();

                @Getter @Builder
                public static class Options {
                    /** <p>The follow options only apply if the editor is set to "monaco"
                     *
                     * theme - must match the file name of a theme in
                     * packages/node_modules/@node-red/editor-client/src/vendor/monaco/dist/theme
                     * e.g. "tomorrow-night", "upstream-sunburst", "github", "my-theme"</p>
                     */
                    @Builder.Default
                    private String theme = "vs";
                    /**
                     * <p>other overrides can be set e.g. fontSize, fontFamily, fontLigatures etc.
                     * for the full list, see <a href="https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandaloneeditorconstructionoptions.html">https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandaloneeditorconstructionoptions.html</a></p>
                     */
                    private Integer fontSize;
                    private String fontFamily;
                    private boolean fontLigatures;
                }
            }
        }
    }
}
