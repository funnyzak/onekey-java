package com.github.funnyzak.web.config.shiro;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.nutz.lang.Lang;
import com.github.funnyzak.biz.ext.shiro.SINOAdvisor;
import com.github.funnyzak.biz.ext.shiro.ShiroSessionManager;
import com.github.funnyzak.biz.ext.shiro.realm.SINORealm;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class ShiroConfiguration {

	private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

//	@Bean
//	public SINOCredentialsMatcher sinoCredentialsMatcher() {
//		return new SINOCredentialsMatcher();
//	}


	@Bean
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 自定义sessionManager
	 *
	 * @return
	 */
	@Bean
	public SessionManager sessionManager() {
		ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
		//这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
		shiroSessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
		return shiroSessionManager;
	}

	@Bean
	@ConditionalOnBean({SINORealm.class, CredentialsMatcher.class})
	public DefaultWebSecurityManager getDefaultWebSecurityManager(SINORealm afdiRealm,
																  CredentialsMatcher credentialsMatcher) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		afdiRealm.setCredentialsMatcher(credentialsMatcher);

		dwsm.setRealms(Lang.list(afdiRealm));
		// 添加自定义SessionManager
		dwsm.setSessionManager(sessionManager());

		dwsm.setRememberMeManager(cookieRememberMeManager());

		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		dwsm.setSubjectDAO(subjectDAO);

		return dwsm;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	public CookieRememberMeManager cookieRememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		simpleCookie.setMaxAge(259200000);
		cookieRememberMeManager.setCookie(simpleCookie);
		cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
		return cookieRememberMeManager;
	}

	@Bean
	@ConditionalOnBean(SecurityManager.class)
	public Advisor getAuthorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		SINOAdvisor aasa = new SINOAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	@Bean(name = "shiroFilter")
	@ConditionalOnBean(SecurityManager.class)
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean
				.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl("/user/login");
		filterChainDefinitionMap.put("/captcha", "anon");
		filterChainDefinitionMap.put("/api-docs", "anon");
		filterChainDefinitionMap.put("/v2/api-docs", "anon");
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/swagger-resources/**", "anon");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}
