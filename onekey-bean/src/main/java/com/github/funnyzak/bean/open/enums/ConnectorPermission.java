package com.github.funnyzak.bean.open.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * 连接器前端使用权限配置
 */
public enum ConnectorPermission {
    NONE(),

    APP_IMAGE_UPLOAD(1, "app.image.upload", "上传图片", "app"),
    APP_SMS_SEND(1, "app.sms.send", "发送短信", "app"),

    MEMBER_CONNECT(1, "member.connect", "会员连接", "member"),;

    ConnectorPermission(){

    }

    ConnectorPermission(Integer type, String name, String description, String group, String intro) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.group = group;
        this.intro = intro;
    }

    ConnectorPermission(Integer type, String name, String description, String group) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.group = group;
    }

    /**
     * 权限类型 1前端 2后端
     */
    @Getter
    @Setter
    private Integer type = 1;

    /**
     * 权限名称/标识
     */
    @Getter
    @Setter
    private String name;

    /**
     * 权限页面显示名称
     */
    @Getter
    @Setter
    private String description;

    /**
     * 权限分组，英文
     */
    @Getter
    @Setter
    private String group;

    /**
     * 该权限的说明/提示信息
     */
    @Getter
    @Setter
    private String intro;
}
