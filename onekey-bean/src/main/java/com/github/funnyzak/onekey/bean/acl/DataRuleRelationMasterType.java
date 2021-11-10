package com.github.funnyzak.onekey.bean.acl;

/**
 * 数据权限的权限主体类型
 */
public enum DataRuleRelationMasterType {
    USER("用户"), ROLE("角色");
    /**
     * 中文描述,主要用于页面展示
     */
    private String name;

    DataRuleRelationMasterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}