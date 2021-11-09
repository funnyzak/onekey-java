package com.github.funnyzak.biz.service.acl;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.springframework.stereotype.Service;

import com.github.funnyzak.bean.acl.RolePermission;

import java.util.List;


@Service
public class RolePermissionService extends BaseService<RolePermission> {
    /**
     * 获取角色所有权限列表
     *
     * @param id
     * @return
     */
    public List<String> listDirectPermissionsByRoleId(long id) {
        Sql sql = dao().sqls().create("list.role.permission.by.role.id");
        sql.params().set("roleId", id);
        sql.setCallback(Sqls.callback.strList());
        dao().execute(sql);
        return sql.getList(String.class);
    }
}
