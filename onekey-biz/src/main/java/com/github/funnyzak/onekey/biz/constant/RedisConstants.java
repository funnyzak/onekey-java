package com.github.funnyzak.onekey.biz.constant;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/16 10:24 上午
 * @description RedisConstants
 */
public interface RedisConstants {

    interface Expire {
        /**
         * KEY默认过期时间
         */
        Integer KEY_EXPIRE = 36000;

        Integer CONNECTOR_EXPIRE = 10800;

        /**
         * 令牌过期时间
         */
        Integer T_TOKEN_EXPIRE = 3600;
    }

    interface Keys {
        /**
         * 连接器Key。变量：secretId
         */
        String CONNECTOR_KEY = "connector:%s";

        /**
         * 令牌Key。变量：token
         */
        String T_TOKEN_KEY = "token:%s";

        /**
         * 会员KEY。变量：appId/key/value
         */
        String MEMBER_KEY = "member:%s:%s:%s";
    }
}