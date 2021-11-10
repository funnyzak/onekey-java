package com.github.funnyzak.onekey.biz.service.acl;

import com.github.funnyzak.onekey.biz.constant.JsonConstants;
import com.github.funnyzak.onekey.biz.service.log.LoginLogService;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import com.github.funnyzak.onekey.bean.acl.Permission;
import com.github.funnyzak.onekey.bean.acl.Role;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.log.LoginLog;
import com.github.funnyzak.onekey.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Potato
 */
@Service
public class ShiroUserService {
    private final Log log = Logs.get();

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final LoginLogService loginLogService;

    @Autowired
    public ShiroUserService(UserService userService, RoleService roleService, PermissionService permissionService, LoginLogService loginLogService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.loginLogService = loginLogService;
    }

    /**
     * 检查权限
     *
     * @param permission 权限名称
     * @param id         用户 id
     * @return 用户是否有参数权限的标识
     */
    public boolean checkPermission(String permission, int id) {

        for (String p : getAllPermissionsInfo(id)) {
            if (Strings.equals(p, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查角色
     *
     * @param role 角色名称
     * @param id   用户 id
     * @return 用户是否有参数角色的标识
     */
    public boolean checkRole(String role, int id) {
        for (String r : getRolesInfo(id)) {
            if (Strings.equals(role, r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户
     */
    public User findByName(String userName) {
        return userService.fetch(userName);
    }

    /**
     * 查询用户的全部权限
     *
     * @param id 用户 id
     * @return 权限列表
     */
    public List<Permission> getAllPermissions(long id) {
        return permissionService.getAllPermissionsByUserId(id);
    }

    /**
     * 根据用户获取权限信息
     *
     * @param id 用户 id
     * @return 权限名称列表
     */
    public List<String> getAllPermissionsInfo(long id) {
        List<Permission> permissions = getAllPermissions(id);
        final List<String> target = Lists.newArrayList();
        Lang.each(permissions, new Each<Permission>() {

            @Override
            public void invoke(int index, Permission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                target.add(ele.getName());
            }
        });
        return target;
    }

    public List<Role> roles(String userName) {
        User user = userService.fetch(userName);
        if (user == null) {
            return Lists.newArrayList();
        }
        return getAllRoles(user.getId());
    }

    public List<Permission> permissions(String userName) {
        User user = userService.fetch(userName);
        if (user == null) {
            return Lists.newArrayList();
        }
        return getAllPermissions(user.getId());
    }

    public List<String> roleInfos(String userName) {
        final List<String> target = Lists.newArrayList();
        Lang.each(roles(userName), new Each<Role>() {

            @Override
            public void invoke(int arg0, Role ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
                target.add(ele.getName());
            }
        });
        return target;
    }

    public List<String> permissionInfos(String userName) {
        final List<String> target = Lists.newArrayList();
        Lang.each(permissions(userName), new Each<Permission>() {

            @Override
            public void invoke(int arg0, Permission ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
                target.add(ele.getName());
            }
        });
        return target;
    }


    /**
     * 获取用户全部角色
     *
     * @param id 用户 id
     * @return 角色列表
     */
    public List<Role> getAllRoles(long id) {
        // XXX 直接权限即全部权限
        return getDirectRoles(id);
    }

    /**
     * 获取用户直接角色
     *
     * @param id 用户 id
     * @return 角色列表
     */
    public List<Role> getDirectRoles(long id) {
        return roleService.listByUserId(id);
    }

    /**
     * 获取用户间接角色
     *
     * @param id 用户 id
     * @return 角色列表
     */
    public List<Role> getIndirectRoles(int id) {
        return Lists.newArrayList();
    }

    /**
     * 获取用户的角色信息列表
     *
     * @param l 用户 id
     * @return 角色名称列表
     */
    public List<String> getRolesInfo(long l) {
        final List<String> roles = Lists.newArrayList();
        Lang.each(getAllRoles(l), new Each<Role>() {

            @Override
            public void invoke(int index, Role ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                roles.add(ele.getName());
            }
        });
        return roles;
    }

    /**
     * 用户登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param ip
     * @return 登录结果
     */
    public Result login(String userName, String password, String ip) {
        try {
            User user = findByName(userName);

            if (user == null) {
                return Result.fail("用户名或密码不存在");
            }
            if (!user.isAvailable()) {
                return Result.fail("账户被锁定");
            }

            Subject currentUser = SecurityUtils.getSubject();

            // 设置session不过期
            currentUser.getSession().setTimeout(-1000L);


            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
            token.setRememberMe(true);
            currentUser.login(token);

            LoginLog log = new LoginLog();
            log.setAddUserId(user.getId());
            log.setIp(ip);
            loginLogService.save(log);

            return Result.success().addData(JsonConstants.AclJson.USER_INFO_NAME, user);
        } catch (LockedAccountException e) {
            log.debug(e);
            return Result.fail("账户被锁定");
        } catch (Exception e) {
            log.debug(e);
            return Result.fail("登录失败");
        }
    }

}
