package com.github.funnyzak.onekey.biz.service.acl;

import org.nutz.dao.sql.Sql;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugin.spring.boot.service.BaseService;
import com.github.funnyzak.onekey.bean.acl.Permission;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * PermissionService class
 *
 * @author potato
 * @date 2019/07/25
 */
@Service
public class PermissionService extends BaseService<Permission> {

    Log log = Logs.get();

    /**
     * 用户的全部权限
     *
     * @param id 用户id
     * @return
     */
    public List<Permission> getAllPermissionsByUserId(long id) {
        List<Permission> target = listDirectPermissionsByUserId(id);
        target.addAll(listIndirectPermissionsByUserId(id));
        return new ArrayList(new HashSet(target));
    }

    /**
     * 批量添加权限
     *
     * @param permissions 每行一个 每行格式如： table.list 列表显示
     * @return
     */
    public List<Permission> addPermissionsMult(String permissions) {
        if (permissions == null) return null;
        String[] permissionsStrArr = permissions.split("\n");

        List<Permission> list = new ArrayList<>();
        for (String permissionsStr : permissionsStrArr) {
            try {
                if (permissionsStr.split(" ").length <= 1) continue;
                Permission permission = new Permission();
                permission.setName(permissionsStr.split(" ")[0]);
                permission.setDescription(permissionsStr.split(" ")[1]);
                list.add(save(permission));
            } catch (Exception e) {
                log.errorf("权限添加失败", e.getMessage());
            }
        }
        return list;
    }


    /**
     * 获取用户的直接权限
     *
     * @param id 用户id
     * @return 角色列表
     */
    public List<Permission> listDirectPermissionsByUserId(long id) {
        Sql sql = dao().sqls().create("list.direct.permission.by.user.id");
        sql.params().set("userId", id);
        return searchObj(sql);
    }

    /**
     * 获取用户的间接权限
     *
     * @param id 用户id
     * @return 角色列表
     */
    public List<Permission> listIndirectPermissionsByUserId(long id) {
        Sql sql = dao().sqls().create("list.indirect.permission.by.user.id");
        sql.params().set("userId", id);
        return searchObj(sql);
    }

}
