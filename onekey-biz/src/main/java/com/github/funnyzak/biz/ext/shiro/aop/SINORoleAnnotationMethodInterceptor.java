package com.github.funnyzak.biz.ext.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;


public class SINORoleAnnotationMethodInterceptor extends RoleAnnotationMethodInterceptor {

	public SINORoleAnnotationMethodInterceptor() {
		setHandler(new SINORoleAnnotationHandler());
	}

	public SINORoleAnnotationMethodInterceptor(AnnotationResolver resolver) {
		setHandler(new SINORoleAnnotationHandler());
		setResolver(resolver);
	}
}
