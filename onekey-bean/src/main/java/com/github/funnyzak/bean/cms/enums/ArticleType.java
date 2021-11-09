package com.github.funnyzak.bean.cms.enums;

public enum ArticleType {
    NORMAL("普通文章"),
    VIDEO("视频");

    ArticleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}