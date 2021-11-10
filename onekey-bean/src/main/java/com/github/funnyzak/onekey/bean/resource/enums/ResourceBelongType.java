package com.github.funnyzak.onekey.bean.resource.enums;

public enum ResourceBelongType {
    NORMAL_ATTACH("普通文件"),
    MEMBER_ATTACH("会员文件");

    ResourceBelongType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
