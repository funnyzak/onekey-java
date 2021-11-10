package com.github.funnyzak.onekey.bean.log.enums;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 10:04 上午
 * @description PmServer
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 短消息平台商
 */
public enum SmsServerType {
    JISU("极速API"),
    TENCENT("腾讯服务"),
    ALIYUN("阿里云"),
    SELF("自有平台");

    @Getter
    @Setter
    private String description;

    SmsServerType(String description) {
        this.description = description;
    }
}