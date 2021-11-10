package com.github.funnyzak.onekey.biz.ext.shiro.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.annotation.Logical;

import com.github.funnyzak.onekey.bean.vo.InstalledRole;


@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SINORequiresRoles {
	Logical logical() default Logical.AND;

	InstalledRole[] value();
}
