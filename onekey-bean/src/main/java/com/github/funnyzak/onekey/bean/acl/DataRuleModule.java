package com.github.funnyzak.onekey.bean.acl;

/**
 * @author potato
 */
public enum DataRuleModule {
    DEMO_MODULE("演示模块1", "演示信息表"),
    DEMO_MODULE2("演示模块2", "演示信息表2");


    DataRuleModule(String system, String table) {
        this.system = system;
        this.table = table;
    }

    /**
     * 对应的子系统
     */
    private String system;

    /**
     * 系统下的表
     */
    private String table;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
