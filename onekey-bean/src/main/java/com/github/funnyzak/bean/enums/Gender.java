package com.github.funnyzak.bean.enums;

public enum Gender {
    MAN("男"),
    WOMEN("女");

    Gender(String name) {
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
