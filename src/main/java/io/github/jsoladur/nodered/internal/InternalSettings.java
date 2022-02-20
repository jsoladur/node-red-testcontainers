package io.github.jsoladur.nodered.internal;

import io.github.jsoladur.nodered.settings.Settings;

import java.util.Map;

public final class InternalSettings extends Settings {

    private final Map<String, Object> functionGlobalContext = Map.of();
}
