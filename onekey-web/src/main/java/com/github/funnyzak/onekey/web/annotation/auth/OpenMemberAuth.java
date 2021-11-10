package com.github.funnyzak.onekey.web.annotation.auth;

import org.apache.shiro.authz.annotation.Logical;
import com.github.funnyzak.onekey.bean.open.enums.ConnectorPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在需要登录验证的Controller的方法上使用此注解
 */
@Target({ElementType.METHOD})// 可用在方法名上
@Retention(RetentionPolicy.RUNTIME)// 运行时有效
public @interface OpenMemberAuth {
    /**
     * 所需权限
     *
     * @return
     */
    ConnectorPermission[] permissions() default {ConnectorPermission.NONE};

    /**
     * 权限集合逻辑
     *
     * @return
     */
    Logical logical() default Logical.OR;

    boolean mustLogin() default true;
}

