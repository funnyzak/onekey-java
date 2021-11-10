package com.github.funnyzak.onekey.bean.enums;

/**
 * 藏品关联类型枚举
 */
public enum TypeRelationEnums {

    /**
     * 模块1
     */
    MODULE_1("模块1");

    TypeRelationEnums(String name) {
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
