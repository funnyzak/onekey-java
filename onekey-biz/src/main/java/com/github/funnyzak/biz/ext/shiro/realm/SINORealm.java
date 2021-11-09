package com.github.funnyzak.biz.ext.shiro.realm;

import com.github.funnyzak.biz.ext.shiro.matcher.SINOCredentialsMatcher;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.lang.Lang;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.enums.UserStatus;
import com.github.funnyzak.biz.service.acl.ShiroUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SINORealm extends AuthorizingRealm {

	@Autowired
	SINOCredentialsMatcher sinoCredentialsMatcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.shiro.realm.AuthenticatingRealm#getCredentialsMatcher()
	 */
	@Override
	public CredentialsMatcher getCredentialsMatcher() {
		return sinoCredentialsMatcher;
	}

	@Autowired
	ShiroUserService shiroUserService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.realm.AuthenticatingRealm#supports(org.apache.shiro.authc.
	 * AuthenticationToken)
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String userName = upToken.getUsername();
		User user = shiroUserService.findByName(userName);
		if (Lang.isEmpty(user))// 用户不存在
			return null;
		if (user.getStatus() == UserStatus.DISABLED)// 用户被锁定
			throw new LockedAccountException("Account [" + upToken.getUsername() + "] is locked.");

		SimpleAuthenticationInfo account = new SimpleAuthenticationInfo(user.getName(), user.getPassword(), getName());

		return account;
	}
	


	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String userName = principalCollection.getPrimaryPrincipal().toString();
		User user = shiroUserService.findByName(userName);
		if (user == null)// 用户不存在
			return null;
		if (user.getStatus() == UserStatus.DISABLED)// 用户被锁定
			throw new LockedAccountException("Account [" + userName + "] is locked.");
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		List<String> roleNameList = shiroUserService.getRolesInfo(user.getId());
		auth.addRoles(roleNameList);// 添加角色
		List<String> permissionNames = shiroUserService.getAllPermissionsInfo(user.getId());
		auth.addStringPermissions(permissionNames);// 添加权限
		return auth;
	}

}
