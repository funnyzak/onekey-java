package com.github.funnyzak.onekey.biz.service.acl;

import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.acl.UserPermission;
import com.github.funnyzak.onekey.bean.acl.UserRole;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class UserService extends GeneralService<User> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserPermissionService userPermissionService;
    private final UserRoleService userRoleService;

    @Autowired
    public UserService(UserPermissionService userPermissionService, UserRoleService userRoleService) {
        this.userPermissionService = userPermissionService;
        this.userRoleService = userRoleService;
    }

    public PageredData<User> userPager(Integer page, Integer pageSize, Cnd cnd) {
        PageredData<User> pager = searchByPage(page <= 0 ? 1 : page, pageSize, (cnd == null ? Cnd.NEW() : cnd).desc("id"));
        return pager;
    }

    public Cnd condition(Cnd cnd, User currentUser, String name, String nickName, String realName) {
        PUtils.cndBySearchKey(cnd, name, "name");
        PUtils.cndBySearchKey(cnd, nickName, "nickName");
        PUtils.cndBySearchKey(cnd, realName, "realName");
        return cnd;
    }

    public <T> List<T> setListAddUserInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getAddUserId", "setAddUser", BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
    }

    public <T> List<T> setListUpdateUserInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getUpdateUserId", "setUpdateUser", BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
    }

    public <T> List<T> setListReviewUserInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getReviewUserId", "setReviewUser", BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
    }


    public <T> List<T> setListInReviewUserInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getInReviewUserId", "setInReviewUser", BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
    }

    /**
     * ????????????????????????????????????LIST???????????????User??????
     *
     * @param list              ???????????????LIST
     * @param getIdMethodName   T??????????????????ID????????????
     * @param setInfoMethodName ???????????????????????????T??????????????????
     * @param <T>               T??????
     * @return ????????????????????????
     * @throws Exception ????????????
     */
    public <T> List<T> setListUserInfo(List<T> list, String getIdMethodName, String setInfoMethodName) {
        return setListInfoByListColumnId(list, getIdMethodName, setInfoMethodName, BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
    }

    public Result changePassword(int id, String old, String newPwd) {
        User user = fetch(id);
        if (user == null) {
            return Result.fail("???????????????!");
        }
        if (!Strings.equals(Lang.md5(old), user.getPassword())) {
            return Result.fail("??????????????????");
        }
        user.setPassword(Lang.md5(newPwd));
        return update(user) == 1 ? Result.success() : Result.fail("??????????????????");
    }


    public List<Record> findPermissionsWithUserPowerdInfoByUserId(int id) {
        Sql sql = dao().sqls().create("find.permissions.with.user.powered.info.by.user.id");
        sql.params().set("id", id);
        return search(sql);
    }


    public List<Record> findRolesWithUserPowerdInfoByUserId(int id) {
        Sql sql = dao().sqls().create("find.roles.with.user.powerd.info.by.user.id");
        sql.params().set("id", id);
        return search(sql);
    }


    public Result setPermission(long[] ids, long userId) {
        /**
         * 1.?????????????????????????????????<br>
         * 2.????????????,????????????????????????,?????????????????????,??????????????????????????????????????????<br>
         * 3.???????????????????????????,????????????
         */
        if (ids == null) {
            ids = new long[]{};
        }
        List<Long> newIds = Lang.array2list(ids, Long.class);
        Collections.sort(newIds);
        List<UserPermission> list = userPermissionService.query(Cnd.where("userId", "=", userId));
        for (UserPermission user : list) {
            int i = 0;
            if ((i = Collections.binarySearch(newIds, user.getPermissionId())) >= 0) {
                newIds.remove(i);
            } else {
                userPermissionService.delete(user.getId());
            }
        }
        for (long pid : newIds) {
            UserPermission userp = new UserPermission();
            userp.setUserId(userId);
            userp.setPermissionId(pid);
            userPermissionService.save(userp);
        }
        return Result.success();
    }


    public Result setRole(long[] ids, long userId) {
        /**
         * 1.?????????????????????????????????<br>
         * 2.????????????,????????????????????????,?????????????????????,??????????????????????????????????????????<br>
         * 3.???????????????????????????,????????????
         */
        if (ids == null) {
            ids = new long[]{};
        }
        List<Long> newIds = Lang.array2list(ids, Long.class);
        Collections.sort(newIds);
        List<UserRole> userRoles = userRoleService.query(Cnd.where("userId", "=", userId));
        for (UserRole role : userRoles) {
            int i = 0;
            if ((i = Collections.binarySearch(newIds, role.getRoleId())) >= 0) {
                newIds.remove(i);
            } else {
                userRoleService.delete(role.getId());
            }
        }
        for (long rid : newIds) {
            UserRole relation = new UserRole();
            relation.setRoleId(rid);
            relation.setUserId(userId);
            userRoleService.save(relation);
        }
        return Result.success();
    }

}
