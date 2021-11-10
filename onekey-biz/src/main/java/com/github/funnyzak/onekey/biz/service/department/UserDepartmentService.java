package com.github.funnyzak.onekey.biz.service.department;

import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.BaseService;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.department.Department;
import com.github.funnyzak.onekey.bean.department.UserDepartment;
import com.github.funnyzak.onekey.common.utils.PUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/21 7:07 PM
 * @description UserDepartmentService
 */
@Service
public class UserDepartmentService extends BaseService<UserDepartment> {
    private final DepartmentService departmentService;

    @Autowired
    public UserDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }



    /**
     * 获取用户的部门设置情况（展现该所有的机构和该用户的设置关系）
     */
    public List<NutMap> findDepartmentsWithUserPowerdInfoByUserId(User user) {
        Sql sql = dao().sqls().create("find.departments.with.user.powerd.info.by.user.id");
        sql.params().set("id", user.getId());
        return searchAsMap(sql);
    }

    /**
     * 获取部门的用户设置设置情况（展现该所有用户和该部门的设置关系）
     */
    public List<NutMap> findUsersWithDepartmentInfoByDepartmentId(Long deptId) {
        Sql sql = dao().sqls().create("find.users.with.department.powerd.info.by.department.id");
        sql.params().set("deptId", deptId);
        return searchAsMap(sql);
    }


    /**
     * 根据用户ID获取所有相关的分支ID
     *
     * @param userId
     * @return
     */
    public List<Long> findDepartmentRelationByUserId(Long userId) {
        List<UserDepartment> list = query(Cnd.where("userId", "=", userId));
        if (list == null || list.size() == 0) {
            return null;
        }

        List<Long> idList = new ArrayList<>();
        for (UserDepartment i : list) {
            idList.add(i.getDepartmentId());
        }
        return idList;
    }

    /**
     * 根据部门ID集合获取关联的所有用户ID
     *
     * @param deptIds
     * @return
     */
    public List<Long> findUserRelationByDepartmentIds(long[] deptIds) {
        if (deptIds.length == 0) {
            return null;
        }

        List<UserDepartment> list = query(Cnd.where("departmentId", "in", deptIds));
        if (list == null || list.size() == 0) {
            return null;
        }

        List<Long> idList = new ArrayList<>();
        for (UserDepartment i : list) {
            idList.add(i.getUserId());
        }
        return idList;
    }

    /**
     * 获取用户关联的分支信息
     */
    public List<NutMap> findRelationDepartmentByUserId(User user) {
        List<NutMap> list = findDepartmentsWithUserPowerdInfoByUserId(user);
        return list == null || list.size() == 0 ? null : list.stream().filter(item -> item.getInt("selected") == 1).collect(Collectors.toList());
    }


    /**
     * 关联用户所属的部门
     *
     * @param ids    部门ID列表
     * @param userId 用户ID
     * @return
     */
    public boolean setDepartment(long[] ids, long userId) {
        if (ids == null || userId <= 0) {
            return false;
        }

        List<Long> newIds = Lang.array2list(ids, Long.class);
        Collections.sort(newIds);

        List<UserDepartment> userDepartments = query(Cnd.where("userId", "=", userId));
        for (UserDepartment info : userDepartments) {
            int i = 0;
            if ((i = Collections.binarySearch(newIds, info.getDepartmentId())) >= 0) {
                newIds.remove(i);
            } else {
                delete(info.getId());
            }
        }
        for (long bid : newIds) {
            UserDepartment relation = new UserDepartment();
            relation.setDepartmentId(bid);
            relation.setUserId(userId);
            save(relation);
        }
        return true;
    }

    /**
     * 设置部门的用户信息
     *
     * @param ids
     * @param deptId
     * @return
     */
    public boolean setDepartmentUsers(long[] ids, long deptId) {
        if (ids == null || deptId <= 0) {
            return false;
        }

        List<Long> newIds = Lang.array2list(ids, Long.class);
        Collections.sort(newIds);

        List<UserDepartment> userDepartments = query(Cnd.where("departmentId", "=", deptId));
        for (UserDepartment info : userDepartments) {
            int i = 0;
            if ((i = Collections.binarySearch(newIds, info.getUserId())) >= 0) {
                newIds.remove(i);
            } else {
                delete(info.getId());
            }
        }
        for (long bid : newIds) {
            UserDepartment relation = new UserDepartment();
            relation.setDepartmentId(deptId);
            relation.setUserId(bid);
            save(relation);
        }
        return true;
    }

    /**
     * 获取用户关联部门的同级别部门所用同事的ID，或包含下级部门所有同事的ID
     *
     * @param user                 主体用户
     * @param includeSubDepartment 是否包含下级部门
     * @return
     */
    public List<Long> allRelationDepartmentUserIdsByUser(User user, Boolean includeSubDepartment) {
        if (user == null ) {
            return null;
        }

        List<Long> userIdList = new ArrayList<>();

        List<Long> deptIds = findDepartmentRelationByUserId(user.getId());
        if (deptIds != null && deptIds.size() > 0) {
            List<Long> uidList = findUserRelationByDepartmentIds(deptIds.stream().mapToLong(id -> id).toArray());
            if (uidList != null && uidList.size() > 0) {
                userIdList.addAll(uidList);
            }
        }

        // 如果包含子部门
        if (includeSubDepartment) {
            List<Department> deptList = departmentService.list();

            List<Department> relationDeptList = new ArrayList<>();
            for (Long deptId : deptIds) {
                List<Department> tempDepts = departmentService.subList(deptList, deptId);
                if (tempDepts == null || tempDepts.size() == 0) {
                    continue;
                }
                relationDeptList.addAll(tempDepts);
            }
            if (relationDeptList != null && relationDeptList.size() > 0) {
                List<Long> uidList = findUserRelationByDepartmentIds(relationDeptList.stream().mapToLong(d -> d.getId()).toArray());
                if (uidList != null && uidList.size() > 0) {
                    userIdList.addAll(uidList);
                }
            }
        }
        return userIdList == null || userIdList.size() == 0 ? userIdList : PUtils.distinctList(userIdList);
    }
}