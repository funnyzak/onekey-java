package com.github.funnyzak.onekey.biz.service.open.impl;

import com.github.funnyzak.onekey.biz.constant.BizConstants;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.open.Connector;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.biz.service.open.ConnectorService;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/11 5:28 下午
 * @description ConnectorServiceImpl
 */
@Service
public class ConnectorServiceImpl extends GeneralService<Connector> implements ConnectorService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private UserService userService;


    @Autowired
    public ConnectorServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public Connector fetch(String secretId) {
        return super.fetch(baseCnd().andEX("secretId", "=", secretId));
    }

    public <T> List<T> setListConnectorInfo(List<T> list, String getIdMethodName, String setInfoMethodName) throws Exception {
        if (list == null || list.size() == 0) {
            return null;
        }

        List<NutMap> userMapList = findNamesByIds(PUtils.getStringArrayByListColumn(list, getIdMethodName));
        return PUtils.setListNutMapColumnByNutMapList(list, userMapList, "id", getIdMethodName, setInfoMethodName);
    }

    @Override
    public <T> List<T> setListConnectorInfo(List<T> list) throws Exception {
        return setListConnectorInfo(list, "getConnectorId", "setConnector");
    }

    /**
     * 根据ID列表获取连接器
     *
     * @param ids
     * @return
     */
    public List<NutMap> findNamesByIds(List<String> ids) {
        Sql sql = dao().sqls().create("list.connector.name.by.ids");
        sql.vars().set("idList", String.join(",", ids));
        sql.setCallback(Sqls.callback.maps());
        dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    /**
     * 获取所有连接器名称
     *
     * @return
     */
    public List<NutMap> allNames() {
        Sql sql = dao().sqls().create("list.connector.name");
        sql.setCallback(Sqls.callback.maps());
        dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @Override
    public Cnd baseCnd() {
        return baseCnd(null);
    }

    @Override
    public Cnd baseCnd(Cnd cnd) {
        return (cnd == null ? Cnd.NEW() : cnd).andEX("del", "=", false);
    }

    @Override
    public Cnd condition(Cnd cnd, User currentUser, String name, String secretId, Boolean enable) {
        cnd = baseCnd(cnd);

        PUtils.cndBySearchKey(cnd, name, "name");
        PUtils.cndBySearchKey(cnd, secretId, "secretId");

        return cnd.andEX("enable", "=", enable);
    }

    @Override
    public PageredData<Connector> pager(Integer page, Integer pageSize, Cnd cnd) {
        return super.searchByPage(page, pageSize, (cnd == null ? baseCnd() : cnd).desc("id"));
    }

    @Override
    public List<Connector> list(Cnd cnd, Integer count) {
        return pager(1, count == null ? 100000 : count, cnd).getDataList();
    }

    @Override
    public String generateSecretId() {
        return StringUtils.getRandomDigital(11);
    }

    @Override
    public String generateSecretKey() {
        return StringUtils.getUUIDNumberOnly();
    }

    @Override
    public Connector add(User currentUser, Connector info) throws Exception {
        if (StringUtils.isNullOrEmpty(info.getAppId())) {
            throw new BizException("请设置AppId");
        }
        info.setSecretId(generateSecretId());
        info.setSecretKey(generateSecretKey());

        if (fetch(info.getSecretId()) != null) {
            throw new BizException("SecretId已存在");
        }
        info.setAddUserId(currentUser.getId());
        info.setAddTime(DateUtils.getTS());
        info = save(info);

        addOperationLog(currentUser, BizConstants.OpenConst.CONNECTOR_NAME, "创建" + BizConstants.OpenConst.CONNECTOR_NAME, info);
        return fetch(info.getId());
    }

    @Override
    public Connector resetSecretKey(User currentUser, String secretId) throws Exception {
        Connector info = fetch(secretId);
        checkNull(info);

        info.setUpdateTime(DateUtils.getTS());
        info.setUpdateUserId(currentUser.getId());
        info.setSecretKey(generateSecretKey());
        update(info);
        addOperationLog(currentUser, BizConstants.OpenConst.NAME, "重置" + BizConstants.OpenConst.CONNECTOR_NAME + "密钥", info);
        return info;
    }

    @Override
    public Connector edit(User currentUser, Connector info) throws Exception {
        info.setUpdateTime(DateUtils.getTS());
        info.setUpdateUserId(currentUser.getId());

        if (!update(info, BizConstants.OpenConst.CONNECTOR_INFO_CAN_EDIT_COLUMN_NAME_LIST.split(","))) {
            throw new BizException("编辑" + BizConstants.OpenConst.CONNECTOR_NAME + "出错");
        }

        addOperationLog(currentUser, BizConstants.OpenConst.NAME, "编辑" + BizConstants.OpenConst.CONNECTOR_NAME, info);
        return fetch(info.getId());
    }

    @Override
    public void remove(User currentUser, String secretId) throws Exception {
        Connector info = fetch(secretId);
        info.setDel(true);

        if (update(info) <= 0) {
            throw new BizException("删除" + BizConstants.OpenConst.CONNECTOR_NAME + "出错");
        }
        addOperationLog(currentUser, BizConstants.OpenConst.NAME, "删除" + BizConstants.OpenConst.CONNECTOR_NAME, info);
    }

    @Override
    public List<Connector> setListInfo(List<Connector> list, Boolean isSetAddUser, Boolean isSetUpdateUser) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (isSetAddUser != null && isSetAddUser) {
            list = userService.setListAddUserInfo(list);
        }

        if (isSetUpdateUser != null && isSetUpdateUser) {
            list = userService.setListUpdateUserInfo(list);
        }
        return list;
    }

    @Override
    public List<Connector> setListInfo(List<Connector> list) {
        return setListInfo(list, true, false);
    }
}