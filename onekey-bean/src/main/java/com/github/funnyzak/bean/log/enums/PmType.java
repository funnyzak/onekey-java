package com.github.funnyzak.bean.log.enums;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 10:03 上午
 * @description PmType
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 短消息类型
 */
public enum PmType {
    SMS("短信"),
    EMAIL("邮件");

    @Getter
    @Setter
    private String description;

    PmType(String description) {
        this.description = description;
    }
}