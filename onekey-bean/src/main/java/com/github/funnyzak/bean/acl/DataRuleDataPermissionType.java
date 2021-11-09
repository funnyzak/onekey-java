package com.github.funnyzak.bean.acl;

/**
 * 数据权限类型
 */
public enum DataRuleDataPermissionType {
    ONLY_MINE("仅自身相关数据", 100),
    ONLY_MY_DEPARTMENT("仅自身部门", 200),
    MY_DEPARTMENT_AND_SUB("自身部门和下级部门", 300),
    ALL("所有", 500);

    DataRuleDataPermissionType(String name, Integer order) {
        this.name = name;
        this.order = order;
    }

    /**
     * 权重大小，越大权限越大
     */
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
