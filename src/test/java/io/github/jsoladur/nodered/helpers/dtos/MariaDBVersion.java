package io.github.jsoladur.nodered.helpers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MariaDBVersion {

    @JsonProperty("VERSION")
    private String version;
}
