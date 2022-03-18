package io.github.jsoladur.nodered.internal.vo;

import io.github.jsoladur.nodered.vo.Settings;
import lombok.*;

import java.util.Map;

@Getter @Setter
public final class InternalSettings extends Settings {

    private final Map<String, Object> functionGlobalContext = Map.of();
}
