package com.github.funnyzak.onekey.bean.enums;

/**
 * 审核动作
 */
public enum ReviewAction {
    TO_REVIEW("审核"),
    TO_SUBMIT("提交");


    ReviewAction(String name) {
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
