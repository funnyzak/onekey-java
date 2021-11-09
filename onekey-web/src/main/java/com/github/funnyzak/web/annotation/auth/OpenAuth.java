package com.github.funnyzak.web.annotation.auth;

import org.apache.shiro.authz.annotation.Logical;
import com.github.funnyzak.bean.open.enums.ConnectorPermission;

import java.lang.annotation.*;

/**
 * @author Leon Yang
 * @date 2019/10/18
 * <p>
 * 开放API注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OpenAuth {
    String system() default "OpenService";

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
}
