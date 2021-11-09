package com.github.funnyzak.bean.resource.enums;

public enum WatermarkWay {
    NONE("无"),
    TEXT("文字"),
    IMAGE("图片");

    WatermarkWay(String name) {
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