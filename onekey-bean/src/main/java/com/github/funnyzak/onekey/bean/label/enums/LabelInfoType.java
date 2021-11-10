package com.github.funnyzak.onekey.bean.label.enums;

/**
 * 标签类型
 *
 * @author potato
 */
public enum LabelInfoType {
    NONE("无分类"),
    TEMP_CATE("临时分类"),
    ARTICLE_CATE("文章分类"),
    VIDEO_CATE("视频分类");


    LabelInfoType(String name) {
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
