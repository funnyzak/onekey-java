package com.github.funnyzak.onekey.biz.service.acl;

import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import com.github.funnyzak.onekey.biz.service.department.UserDepartmentService;
import com.github.funnyzak.onekey.bean.acl.*;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
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
 * @description DataRuleRelationService
 */
@Service
public class DataRuleRelationService extends GeneralService<DataRuleRelation> {

    private final ShiroUserService shiroUserService;
    private final DataRuleService dataRuleService;
    private final UserService userService;
    private final UserDepartmentService userDepartmentService;

    @Autowired
    public DataRuleRelationService(ShiroUserService shiroUserService,
                                   DataRuleService dataRuleService,
                                   UserService userService,
                                   UserDepartmentService userDepartmentService
    ) {
        this.shiroUserService = shiroUserService;
        this.dataRuleService = dataRuleService;
        this.userDepartmentService = userDepartmentService;
        this.userService = userService;
    }

    /**
     * 获取主体（用户或角色）和所有数据权限的设置情况
     */
    public List<NutMap> findDataRulesWithUserPowerdInfoByUserId(Long masterId, DataRuleRelationMasterType masterType) {
        Sql sql = dao().sqls().create("find.datarules.with.master.powerd.info.by.master.id");
        sql.params().set("masterType", masterType);
        sql.params().set("masterId", masterId);
        return searchAsMap(sql);
    }

    /**
     * 设置主体的数据权限
     *
     * @param ids        数据权限ID
     * @param masterId   主体ID
     * @param masterType 主体类型
     * @return
     */
    public boolean setDataRules(long[] ids, long masterId,DataRuleRelationMasterType masterType) {
        if (ids == null || masterId <= 0) {
            return false;
        }

        List<Long> newIds = Lang.array2list(ids, Long.class);
        Collections.sort(newIds);

        List<DataRuleRelation> dataRuleRelations = query(Cnd.where("dataRuleRelationMaster", "=", masterType).and("dataRuleRelationId", "=", masterId));
        for (DataRuleRelation info : dataRuleRelations) {
            int i = 0;
            if ((i = Collections.binarySearch(newIds, info.getDataRuleId())) >= 0) {
                newIds.remove(i);
            } else {
                delete(info.getId());
            }
        }
        for (long bid : newIds) {
            DataRuleRelation relation = new DataRuleRelation();
            relation.setDataRuleId(bid);
            relation.setDataRuleRelationId(masterId);
            relation.setDataRuleRelationMaster(masterType);
            save(relation);
        }
        return true;
    }

    /**
     * 根据用户ID获取用户的直接数据权限ID
     */
    public long[] directDataRuleIdsByUid(Long userId) {
        List<DataRuleRelation> list = query(Cnd.where("dataRuleRelationMaster", "=", DataRuleRelationMasterType.USER).andEX("dataRuleRelationId", "=", userId));
        return list == null || list.size() == 0 ? null : list.stream().mapToLong(item -> item.getDataRuleId()).toArray();
    }

    /**
     * 根据用户角色列表ID获取数据权限ID列表
     */
    public long[] directDataRuleIdsByUserRoleIds(long[] roleIds) {
        List<DataRuleRelation> list = query(Cnd.where("dataRuleRelationMaster", "=", DataRuleRelationMasterType.ROLE).andEX("dataRuleRelationId", "in", roleIds));
        return list == null || list.size() == 0 ? null : list.stream().mapToLong(item -> item.getDataRuleId()).toArray();
    }

    /**
     * 通过用户ID，查询用户的角色进而获取用户所有数据权限ID
     *
     * @param userId
     * @return
     */
    public long[] directDataRuleIdsByUserRoleIds(Long userId) {
        List<Role> list = shiroUserService.getAllRoles(userId);
        if (list == null || list.size() == 0) {
            return null;
        }

        return directDataRuleIdsByUserRoleIds(list.stream().mapToLong(item -> item.getId()).toArray());
    }

    /**
     * 根据用户ID，获取他的所有数据权限ID
     */
    public long[] allDataRuleIdsByUserId(Long userId) {
        long[] idByUser = directDataRuleIdsByUid(userId);
        long[] idByRole = directDataRuleIdsByUserRoleIds(userId);
        List<Long> newIdList = new ArrayList<>();
        if (idByUser != null) {
            for (long id : idByUser) {
                newIdList.add(id);
            }
        }
        if (idByRole != null) {
            for (long id : idByRole) {
                newIdList.add(id);
            }
        }
        return newIdList.size() == 0 ? null : newIdList.stream().mapToLong(item -> item.longValue()).toArray();
    }

    /**
     * 获取用户的所有数据权限实体列表
     */
    public List<DataRule> allDataRuleByUserId(Long userId, DataRuleModule dataRuleModule) {
        long[] ruleIds = allDataRuleIdsByUserId(userId);
        if (ruleIds == null) {
            return null;
        }
        return dataRuleService.query(Cnd.where("id", "in", ruleIds).andEX("module", "=", dataRuleModule));
    }

    /**
     * 获取用户匹配的最大权限规则
     */
    public DataRule matchMaxPermissionRuleByUserId(Long userId, DataRuleModule dataRuleModule) {
        List<DataRule> list = allDataRuleByUserId(userId, dataRuleModule);
        if (list == null || list.size() == 0) {
            return null;
        }
        list.sort(new DataRule.DataRuleComparator());
        return list.get(0);
    }

    /**
     * 根据用户数据权限获取简单信息列表
     *
     * @param user
     * @param dataRuleModule
     * @return
     */
    public List<NutMap> simpleUserInfoMapListByUserDataRule(User user, DataRuleModule dataRuleModule) {
        List<Long> uidList = userIdsByUserDataRule(user, dataRuleModule);
        if (uidList != null && uidList.size() > 0) {
            return userService.findInfosByIds(uidList, BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST);
        } else {
            List<User> list = userService.query(Cnd.NEW());
            if (list == null || list.size() == 0) {
                return null;
            }
            return list.stream().map(v -> PUtils.entityToNutMap(v, BizConstants.UserConst.SIMPLE_USER_INFO_FIELD_NAME_LIST)).collect(Collectors.toList());
        }
    }

    /**
     * 根据用户的数据权限设置情况，获取所关联的用户ID列表
     *
     * @param user           用户ID
     * @param dataRuleModule 所在权限模块
     * @return
     */
    public List<Long> userIdsByUserDataRule(User user, DataRuleModule dataRuleModule) {

        // 如果非用户则无需获取数据权限相关信息，直接可看功能所有信息
        if (user == null) {
            return null;
        }

        DataRule dataRule = matchMaxPermissionRuleByUserId(user.getId(), dataRuleModule);
        if (dataRule == null || dataRule.getRuleType().equals(DataRuleDataPermissionType.ALL)) {
            return null;
        }
        List<Long> userIds = new ArrayList<>();

        switch (dataRule.getRuleType()) {
            case ONLY_MINE:
                userIds.add(user.getId());
                break;
            case ONLY_MY_DEPARTMENT:
                userIds = userDepartmentService.allRelationDepartmentUserIdsByUser(user, false);
                break;
            case MY_DEPARTMENT_AND_SUB:
                userIds = userDepartmentService.allRelationDepartmentUserIdsByUser(user, true);
                break;
            default:
                break;
        }
        return userIds;
    }

    public Cnd dataRulePermissionCondition(Cnd cnd, User user, DataRuleModule dataRuleModule) {
        return dataRulePermissionCondition(cnd, user, dataRuleModule, "addUserId");
    }

    /**
     * 获取用户数据权限查询条件
     */
    public Cnd dataRulePermissionCondition(Cnd cnd, User user, DataRuleModule dataRuleModule, String userIdFieldName) {
        cnd = cnd == null ? Cnd.NEW() : cnd;
        if (user != null) {
            List<Long> uidList = userIdsByUserDataRule(user, dataRuleModule);
            if (uidList != null && uidList.size() > 0) {
                cnd.andEX(userIdFieldName, "in", uidList);
            }
        }
        return cnd;
    }
}