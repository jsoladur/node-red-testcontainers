package com.github.jsoladur.nodered.helpers.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Posts {

    private Integer userId;
    private Integer id;
    private String title;
    private String body;
}
