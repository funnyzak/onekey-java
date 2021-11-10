package com.github.funnyzak.onekey.biz.ext.shiro.anno;

import org.apache.shiro.authz.annotation.Logical;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author potato
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {
	Logical logical() default Logical.AND;

	InstallPermission[] value();
}
