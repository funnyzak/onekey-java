package com.github.funnyzak.onekey.bean.open.enums;

import lombok.Getter;
import lombok.Setter;

@Deprecated
public enum ConnectPower {
    OPEN("上下行不限制"),
    REJECT("上下行限制"),
    DOWNSTREAM("上行限制"),
    UPSTREAM("下行限制");

    ConnectPower(String name) {
        this.name = name;
    }

    @Getter
    @Setter
    private String name;
}