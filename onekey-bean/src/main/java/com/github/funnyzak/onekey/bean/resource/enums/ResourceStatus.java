package com.github.funnyzak.onekey.bean.resource.enums;

public enum ResourceStatus {
    PROGRESSING("处理中"),
    FAIL("失败"),
    SUCCESS("成功");

    ResourceStatus(String name) {
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
