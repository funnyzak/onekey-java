package com.github.funnyzak.onekey.web;

import com.github.funnyzak.onekey.bean.acl.*;
import com.github.funnyzak.onekey.biz.service.acl.*;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import com.github.funnyzak.onekey.bean.enums.UserStatus;
import com.github.funnyzak.onekey.bean.vo.InstallPermission;
import com.github.funnyzak.onekey.bean.vo.InstalledRole;
import com.github.funnyzak.onekey.biz.ext.shiro.matcher.SINOCredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.github.funnyzak.onekey")
//@SpringBootApplication(exclude = {RedisAutoConfiguration.class},scanBasePackages = "com.github.funnyzak")
@EnableAsync
@EnableTransactionManagement
public class WebApplication {
    final static Logger log = LoggerFactory.getLogger(WebApplication.class);
    public static final String CAPTCHA_KEY = "SKYF_CAPTCHA";
    public static final String USER_KEY = "SKYF_USER_KEY";

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(WebApplication.class);
        application.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

            Role admin;

            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                // 这里的逻辑将在应用启动之后执行
                ApplicationContext context = event.getApplicationContext();
                Dao dao = context.getBean(Dao.class);
                if (context.getParent() == null) {
                    log.debug("application starter...");
                    // 确保表结构正确
                    Daos.createTablesInPackage(dao, "com.github.funnyzak.bean", false);
                    Daos.migration(dao, "com.github.funnyzak.bean", true, true);
                    initAcl(context);
                }
            }

            private void initAcl(ApplicationContext context) {
//                log.debug("init acl...");
                final UserService userService = context.getBean(UserService.class);
                final RoleService roleService = context.getBean(RoleService.class);
                final PermissionService permissionService = context.getBean(PermissionService.class);
                final UserRoleService userRoleService = context.getBean(UserRoleService.class);
                final RolePermissionService rolePermissionService = context.getBean(RolePermissionService.class);

                // 内置角色
                Lang.each(InstalledRole.values(), (Each<InstalledRole>) (index, role, length) -> {
                    if (roleService.fetch(Cnd.where("name", "=", role.getName())) == null) {
                        Role temp = new Role();
                        temp.setName(role.getName());
                        temp.setDescription(role.getDescription());
                        temp.setInstalled(true);
                        roleService.save(temp);
                    }
                });

                admin = roleService.fetch(Cnd.where("name", "=", InstalledRole.SU.getName()));

                // 这里理论上是进不来的,防止万一吧
                if (admin == null) {
                    admin = new Role();
                    admin.setName(InstalledRole.SU.getName());
                    admin.setDescription(InstalledRole.SU.getDescription());
                    admin = roleService.save(admin);
                }
                // 内置权限
                Lang.each(InstallPermission.values(), (int index, InstallPermission permission, int length) -> {
                    Permission temp = null;
                    if ((temp = permissionService.fetch(Cnd.where("name", "=", permission.getName()))) == null) {
                        temp = new Permission();
                        temp.setName(permission.getName());
                        temp.setDescription(permission.getDescription());
                        temp.setGroup(permission.getGroup());
                        temp.setIntro(permission.getIntro());
                        temp.setInstalled(true);
                        temp = permissionService.save(temp);
                    }

                    // 给SU授权
                    if (rolePermissionService.fetch(Cnd.where("permissionId", "=", temp.getId()).and("roleId", "=", admin.getId())) == null) {
                        RolePermission rp = new RolePermission();
                        rp.setRoleId(admin.getId());
                        rp.setPermissionId(temp.getId());
                        rolePermissionService.save(rp);
                    }
                });

                User surperMan = null;
                if ((surperMan = userService.fetch(Cnd.where("name", "=", "admin"))) == null) {
                    surperMan = new User();
                    surperMan.setEmail("hello@world.com");
                    surperMan.setName("admin");
                    surperMan.setPassword(SINOCredentialsMatcher.password("admin", "1234567q.."));
                    surperMan.setPhone("13888888888");
                    surperMan.setRealName("李思");
                    surperMan.setNickName("思来想去");
                    surperMan.setStatus(UserStatus.ACTIVE);
                    surperMan = userService.save(surperMan);
                }

                UserRole ur = null;
                if ((ur = userRoleService.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId()))) == null) {
                    ur = new UserRole();
                    ur.setUserId(surperMan.getId());
                    ur.setRoleId(admin.getId());
                    userRoleService.save(ur);
                }
            }

        });
        application.run(args);
    }
}
