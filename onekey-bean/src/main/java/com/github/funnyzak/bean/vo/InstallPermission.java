package com.github.funnyzak.bean.vo;

/**
 * 系统级默认安装权限数据，逻辑块需写入对应权限代码
 */
public enum InstallPermission {
    /**
     * ++++++++++++++++++++++访问控制++++++++++++++++++++++++++++++++
     */
    USER_LIST("user.list", "用户列表", "acl"),
    USER_ADD("user.add", "用户添加", "acl"),
    USER_ACTIVE("user.active", "用户启用", "acl"),
    USER_DISABLED("user.disabled", "用户禁用", "acl"),
    USER_DETAIL("user.detail", "用户详情", "acl"),
    USER_ROLE("user.role", "用户设置角色", "acl"),
    USER_GRANT("user.grant", "用户设置权限", "acl"),
    USER_EDIT("user.edit", "用户编辑", "acl"),
    USER_SETTING_PASSWORD("user.setting.password", "用户重设密码", "acl"),
    USER_DELETE("user.delete", "用户删除", "acl"),
    USER_EXPORT("user.export", "用户信息导出", "acl"),
    USER_DEPARTMENT("user.department", "用户设置分支机构", "acl"),
    USER_DATA_RULE("user.data.rule", "用户设置数据权限", "acl"),

    ROLE_LIST("role.list", "角色列表", "acl"),
    ROLE_ADD("role.add", "角色添加", "acl"),
    ROLE_GRANT("role.grant", "角色设置权限", "acl"),
    ROLE_EDIT("role.edit", "角色编辑", "acl"),
    ROLE_DELETE("role.delete", "角色删除", "acl"),
    ROLE_DATA_RULE("role.data.rule", "角色设置数据权限", "acl"),

    PERMISSION_LIST("permission.list", "权限列表", "acl"),
    PERMISSION_ADD("permission.add", "权限添加", "acl"),
    PERMISSION_EDIT("permission.edit", "编辑权限", "acl"),
    PERMISSION_DELETE("permission.delete", "删除权限", "acl"),

    DATA_RULE_LIST("data.permission.list", "数据权限列表", "acl"),
    DATA_RULE_ADD("data.permission.add", "数据权限添加", "acl"),
    DATA_RULE_EDIT("data.permission.edit", "编辑数据权限", "acl"),
    DATA_RULE_DELETE("data.permission.delete", "删除数据权限", "acl"),

    /**
     * ++++++++++++++++++++++标签管理++++++++++++++++++++++++++++++++
     */
    LABEL_INFO_LIST("label.info.list", "标签列表", "label.info"),
    LABEL_INFO_ADD("label.info.add", "添加标签", "label.info"),
    LABEL_INFO_EDIT("label.info.edit", "编辑标签", "label.info"),
    LABEL_INFO_DELETE("label.info.delete", "删除标签", "label.info"),
    LABEL_INFO_DETAIL("label.info.detail", "标签详情", "label.info"),

    ARTICLE_LABEL_SETTING("label.info.article.cate", "文章分类管理", "label.info", "此功能需配置标签管理权限"),

    /**
     * ++++++++++++++++++++++内容模块++++++++++++++++++++++++++++++++
     */
    CMS_ARTICLE_INFO_LIST("cms.article.list", "文章列表", "cms"),
    CMS_ARTICLE_INFO_ADD("cms.article.add", "添加文章", "cms"),
    CMS_ARTICLE_INFO_EDIT("cms.article.edit", "编辑文章", "cms"),
    CMS_ARTICLE_INFO_DELETE("cms.article.delete", "删除文章", "cms"),
    CMS_ARTICLE_INFO_DETAIL("cms.article.detail", "文章详情", "cms"),

    /**
     * ++++++++++++++++++++++配置管理++++++++++++++++++++++++++++++++
     */
    CONFIG_LIST("config.list", "配置管理", "config"),
    CONFIG_ADD("config.add", "配置添加", "config"),
    CONFIG_EDIT("config.edit", "配置编辑", "config"),
    CONFIG_DELETE("config.delete", "配置删除", "config"),

    /**
     * 分支管理
     */
    DEPT_LIST("dept.list", "部门列表", "dept"),
    DEPT_ADD("dept.add", "部门添加", "dept"),
    DEPT_EDIT("dept.edit", "部门编辑", "dept"),
    DEPT_DELETE("dept.delete", "部门删除", "dept"),
    DEPT_USER_EDIT("dept.user.edit", "部门员工设置", "dept"),

    /**
     * ++++++++++++++++++++++连接器模块++++++++++++++++++++++++++++++++
     */
    CONNECTOR_LIST("connector.list", "连接器管理", "connector"),
    CONNECTOR_ADD("connector.add", "连接器添加", "connector"),
    CONNECTOR_EDIT("connector.edit", "连接器编辑", "connector"),
    CONNECTOR_DELETE("connector.delete", "连接器删除", "connector"),
    CONNECTOR_LOG_LIST("connector.log.list", "连接器日志", "connector"),

    /**
     * ++++++++++++++++++++++会员信息++++++++++++++++++++++++++++++++
     */
    MEMBER_INFO_LIST("member.info.list", "会员列表", "member.info"),
    MEMBER_INFO_ADD("member.info.add", "添加会员", "member.info"),
    MEMBER_INFO_EDIT("member.info.edit", "编辑会员", "member.info"),
    MEMBER_INFO_DELETE("member.info.delete", "删除会员", "member.info"),
    MEMBER_INFO_DETAIL("member.info.detail", "会员详情", "member.info"),
    MEMBER_INFO_REVIEW("member.info.review", "审核会员信息", "member.info"),
    MEMBER_INFO_RESET_PWD("member.info.reset.pwd", "重置会员密码", "member.info"),

    /**
     * ++++++++++++++++++++++系统维护相关++++++++++++++++++++++++++++++++
     */
    MONITOR_DATABASE("system.database.monitor", "数据库", "system"),
    MONITOR_OPERATION("system.operation.log", "操作历史", "system"),
    MONITOR_PM_LOG("system.pm.log", "短信日志", "system"),
    MONITOR_LOGIN("system.login.log", "登陆追踪", "system");


    /**
     * 权限名称/标识
     */
    private String name;

    /**
     * 权限页面显示名称
     */
    private String description;

    /**
     * 权限分组，英文
     */
    private String group;

    /**
     * 该权限的说明/提示信息
     */
    private String intro;

    InstallPermission(String name, String description, String group, String intro) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.intro = intro;
    }

    InstallPermission(String name, String description, String group) {
        this.name = name;
        this.description = description;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

}
