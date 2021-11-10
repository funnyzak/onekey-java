package com.github.funnyzak.onekey.bean.enums;

/**
 * 审核状态
 */
public enum ReviewStatus {
    WAIT_SUBMIT("待提交"),
    REVIEWING("审核中"),
    FAIL("不合格"),
    SUCCESS("已通过");


    ReviewStatus(String name) {
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
