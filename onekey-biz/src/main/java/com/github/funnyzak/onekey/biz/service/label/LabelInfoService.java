package com.github.funnyzak.onekey.biz.service.label;

import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.label.LabelInfo;
import com.github.funnyzak.onekey.bean.label.enums.LabelInfoType;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelInfoService extends GeneralService<LabelInfo> {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    @Autowired
    public LabelInfoService(UserService userService) {
        this.userService = userService;
    }

    public LabelInfo exist(LabelInfoType labelType, String name) {
        return fetch(Cnd.NEW().andEX("name", "=", name).andEX("type", "=", labelType).and("del", "=", false));
    }

    public LabelInfo fetch(Long id) {
        return fetch(Cnd.where("id", "=", id).andEX("del", "=", false));
    }

    /**
     * 标签数据初始化
     *
     * @throws Exception
     */
    public void initLabelDate() throws Exception {
        // 删除前10000个保留系统项
        dao().clear(LabelInfo.class, Cnd.where("id", "<=", 10000));

        // 重新插入10000项系统项
        Sql sql = dao().sqls().create("label.info.data.init");
        dao().execute(sql);
    }

    /**
     * 用户删除标签
     *
     * @param id
     * @param currentUser
     * @return
     */
    public void userDelete(Long id, LabelInfoType labelType, User currentUser) throws Exception {
        LabelInfo info = fetch(id);
        if (info == null) {
            throw new BizException("数据不存在");
        }

        if (!info.getType().equals(labelType)) {
            throw new BizException("非法操作");
        }
        if (info.getSystem()) {
            throw new BizException("内置标签无法删除");
        }
        List<LabelInfo> list = query(condition(null, null, null, id, null, null, null));
        if (list != null && list.size() > 0) {
            throw new BizException("请删除下级");
        }

        info.setDel(true);
        if (update(info) <= 0) {
            throw new BizException("删除操作失败");
        }

        addOperationLog(currentUser, labelType.getName() + "管理", labelType.getName() + "删除", info.toString());
    }

    public LabelInfo userEdit(LabelInfo info, LabelInfoType labelType, User currentUser) throws Exception {
        return userEdit(info, labelType, currentUser, true);
    }


    public Cnd condition(Cnd cnd, LabelInfoType labelType, Boolean system, Long parentId, String name, Long startTime, Long endTime) {
        cnd = (cnd == null ? Cnd.NEW() : cnd);
        cnd = PUtils.cndBySearchKey(cnd, name, "num", "name", "value", "description");
        return cnd
                .andEX("type", "=", labelType)
                .andEX("system", "=", system)
                .andEX("parentId", "=", parentId)
                .andEX("del", "=", false)
                .andEX("addTime", ">=", startTime)
                .andEX("addTime", "<=", endTime);
    }


    /**
     * 用户编辑标签
     *
     * @param info        标签信息
     * @param currentUser 当前用户
     * @return 返回操作结果
     */
    public LabelInfo userEdit(LabelInfo info, LabelInfoType labelType, User currentUser, boolean checkExist) throws Exception {
        boolean isAddAction = info.getId() == null || info.getId() <= 0;
        info.setType(labelType);

        validateFormFields(info, currentUser, isAddAction, checkExist);

        info.setUpdateTime(DateUtils.getTS());
        info.setUpdateUserId(currentUser.getId());

        if (isAddAction) {
            info.setAddTime(DateUtils.getTS());
            info.setAddUserId(currentUser.getId());

            info = save(info);
        } else {
            if (!update(info, BizConstants.LabelConst.CAN_EDIT_COLUMN_NAME_LIST.split(","))) {
                throw new BizException("编辑操作失败");
            }
        }

        addOperationLog(currentUser, labelType.getName() + "管理", (isAddAction ? "添加" : "编辑") + labelType.getName(), info.toString());

        return isAddAction ? info : fetch(info.getId());
    }

    /**
     * 用户获取标签列表
     *
     * @param currentUser 当前用户
     * @param page        页码
     * @param pageSize    页大小
     * @return
     */
    public PageredData<LabelInfo> userPage(User currentUser, Integer page, Integer pageSize, Cnd cnd, String descBy) {
        return searchByPage(page, pageSize, cnd.desc(StringUtils.isNullOrEmpty(descBy) ? "id" : descBy));
    }

    public <T> List<T> setListTextureInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getTextureId", "setTexture");
    }

    public <T> List<T> setListYearInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getYearId", "setYear");
    }

    public <T> List<T> setListCateInfo(List<T> list) {
        return setListInfoByListColumnId(list, "getCateId", "setCate");
    }

    public <T> List<T> setListMethodsInfo(List<T> list) {
        return setListInfoByListColumnIds(list, "getMethods", "setMethodList");
    }

    public <T> List<T> setListMaterialsInfo(List<T> list) {
        return setListInfoByListColumnIds(list, "getMaterials", "setMaterialList");
    }


    /**
     * @param info     标签信息
     * @param user     当前用户
     * @param isCreate 是否新建
     * @return
     */
    public void validateFormFields(LabelInfo info, User user, boolean isCreate, boolean checkExist) throws Exception {
        if (StringUtils.isNullOrEmpty(info.getName())) {
            throw new BizException("名称不能为空");
        }
        if (isCreate) {
            if (info.getType() == null
                    || info.getParentId() == null) {
                throw new BizException("数据不完整");
            }
            if (info.getSystem()) {
                throw new BizException("非法操作");
            }
            if (checkExist && exist(info.getType(), info.getName()) != null) {
                throw new BizException("勿重复添加");
            }
        }

        if (!isCreate) {
            LabelInfo realInfo = fetch(info.getId());
            if (realInfo == null) {
                throw new BizException("数据不存在");
            }
            if (checkExist) {
                LabelInfo existLabel = exist(info.getType(), info.getName());
                if (existLabel != null && !existLabel.getId().equals(info.getId())) {
                    throw new BizException("勿重复添加");
                }
            }
        }
    }

    /**
     * 根据标签类型获取关联的的所有标签
     *
     * @param labelType 标签类型可为NULL
     * @return 获取的标签列表
     */
    public List<LabelInfo> allByFunctionType(LabelInfoType labelType) {
        LabelInfoType[] labelTypes = {labelType};
        return allByFunctionType(Arrays.asList(labelTypes));
    }

    public List<LabelInfo> allByFunctionType(List<LabelInfoType> labelTypeList) {
        Cnd cnd = Cnd.NEW().andEX("type", "in", labelTypeList).andEX("del", "=", false);
        return query(cnd);
    }

    /**
     * 根据标签类型获取简单标签列表
     */
    public List<NutMap> simpleListByFunctionType(LabelInfoType labelType) {
        List<LabelInfo> list = allByFunctionType(labelType);
        if (list == null || list.size() == 0) {
            list = new ArrayList<>();
        }

        return list.stream().map(label -> {
            NutMap map = new NutMap();
            map.put("id", label.getId());
            map.put("name", label.getName());
            map.put("parentId", label.getParentId());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 根据Name 查找对应ID
     *
     * @param name      名称
     * @param labelList 要查找的源集合
     * @return
     */
    public Long findIdByName(String name, List<LabelInfo> labelList) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }

        List<LabelInfo> rltList = labelList.stream().filter(v -> v.getName().equals(name)).collect(Collectors.toList());
        return rltList.size() == 0 ? null : rltList.get(0).getId();
    }

    /**
     * 获取指定ID下的所有标签，包含自身
     *
     * @param all      数据源
     * @param parentId 父ID
     * @return
     */
    public List<LabelInfo> subAllList(List<LabelInfo> all, Long parentId) {
        return subAllList(all, parentId, true);
    }

    public List<LabelInfo> subAllList(List<LabelInfo> all, Long parentId, boolean includeSelf) {
        if (all == null || all.size() == 0) {
            return null;
        }
        List<LabelInfo> parentMatch = all.stream().filter(d -> d.getId().equals(parentId)).collect(Collectors.toList());
        if (parentMatch == null || parentMatch.size() == 0) {
            return null;
        }

        List<LabelInfo> list = new ArrayList<>();

        if (includeSelf) {
            list.add(parentMatch.get(0));
        }

        List<LabelInfo> subList = all.stream().filter(d -> d.getParentId().equals(parentId)).collect(Collectors.toList());
        if (subList == null || subList.size() == 0) {
            return list;
        }

        for (LabelInfo d : subList) {
            List<LabelInfo> dList = subAllList(all, d.getId(), true);
            if (dList == null || dList.size() == 0) {
                continue;
            }
            list.addAll(dList);
        }
        return list;
    }
}