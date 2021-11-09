package com.github.funnyzak.bean.open.enums;

import lombok.Getter;
import lombok.Setter;

@Deprecated
public enum ConnectType {
    FRONT("前端"),
    CONSOLE("后端");

    ConnectType(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    private String name;
}