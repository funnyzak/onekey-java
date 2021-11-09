package com.github.funnyzak.bean.log.enums;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 10:04 上午
 * @description PmUse
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 短消息用途
 */
public enum PmUse {
    NOTIFY("通知"),
    LOGIN("登陆"),
    REGISTER("注册"),
    RESET_PHONE("重置手机号"),
    VERIFY_ACTION("验证操作"),
    FORGET_PASSWORD("忘记密码");

    @Getter
    @Setter
    private String description;

    PmUse(String description) {
        this.description = description;
    }
}