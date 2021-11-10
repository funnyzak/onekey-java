package com.github.funnyzak.onekey.biz.ext.shiro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.funnyzak.onekey.biz.ext.shiro.anno.RequiresPermissions;
import com.github.funnyzak.onekey.biz.ext.shiro.anno.SINORequiresRoles;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;


public class SINOAdvisor extends StaticMethodMatcherPointcutAdvisor {

	private static final Class[] AUTHZ_ANNOTATION_CLASSES = { org.apache.shiro.authz.annotation.RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class,
			RequiresPermissions.class, SINORequiresRoles.class };

	protected SecurityManager securityManager = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SINOAdvisor() {
		setAdvice(new SINOAnnotationsAuthorizingMethodInterceptor());
	}

	public SecurityManager getSecurityManager() {
		return this.securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public boolean matches(Method method, Class targetClass) {
		Method m = method;

		if (isAuthzAnnotationPresent(m)) {
			return true;
		}

		if (targetClass != null) {
			try {
				m = targetClass.getMethod(m.getName(), m.getParameterTypes());
				if (isAuthzAnnotationPresent(m)) {
					return true;
				}
			} catch (NoSuchMethodException ignored) {
			}
		}

		return false;
	}

	private boolean isAuthzAnnotationPresent(Method method) {
		for (Class annClass : AUTHZ_ANNOTATION_CLASSES) {
			Annotation a = AnnotationUtils.findAnnotation(method, annClass);
			if (a != null) {
				return true;
			}
		}
		return false;
	}

}
