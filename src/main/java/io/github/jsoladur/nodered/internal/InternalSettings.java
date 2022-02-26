package io.github.jsoladur.nodered.internal;

import io.github.jsoladur.nodered.settings.Settings;
import lombok.*;

import java.util.Map;

@Getter @Setter
public final class InternalSettings extends Settings {

    private final Map<String, Object> functionGlobalContext = Map.of();
}
