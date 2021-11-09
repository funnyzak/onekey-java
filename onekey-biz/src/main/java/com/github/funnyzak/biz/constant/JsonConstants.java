package com.github.funnyzak.biz.constant;

/**
 * @author potato
 * @date 2019/07/26
 */
public interface JsonConstants {

    String PAGER_NAME = "pager";

    String INFO_NAME = "info";

    String LIST_NAME = "list";

    String COUNT_NAME = "count";

    String STAT_NAME = "stat";

    String SELECT_LIST_NAME = "select";

    interface MemberConst {
        String TOKEN_NAME = "token";

        String MEMBER_NAME = "member";

    }

    interface AclJson {
        /**
         * 用户对象名称
         */
        String USER_INFO_NAME = "user";

        /**
         * 用户令牌名称
         */
        String USER_TOKEN_NAME = "userToken";

        /**
         * 角色列表名称
         */
        String ROLE_LIST_NAME = "roles";

        /**
         * 权限列表名称
         */
        String PERMISSION_LIST_NAME = "permissions";

        /**
         * 权限对象名称
         */
        String PERMISSION_NAME = "permission";

        /**
         * 数据权限列表名称
         */
        String DATA_RULE_LIST_NAME = "dataRules";

        /**
         * 数据权限对象名称
         */
        String DATA_RULE_NAME = "dataRule";
    }
}