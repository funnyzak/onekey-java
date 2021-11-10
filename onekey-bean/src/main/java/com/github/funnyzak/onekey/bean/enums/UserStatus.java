package com.github.funnyzak.onekey.bean.enums;

public enum UserStatus {
    ACTIVE("正常"), DISABLED("禁用");
    /**
     * 中文描述,主要用于页面展示
     */
    private String name;

    UserStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
