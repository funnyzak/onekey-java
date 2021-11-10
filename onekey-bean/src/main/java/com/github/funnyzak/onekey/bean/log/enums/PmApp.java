package com.github.funnyzak.onekey.bean.log.enums;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 10:04 上午
 * @description PmApp
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 短消息应用
 */
public enum PmApp {
    SELF_CONSOLE("自有后端"),
    SELF_FRONT("自有前端");

    @Getter
    @Setter
    private String description;

    PmApp(String description) {
        this.description = description;
    }
}