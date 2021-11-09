package com.github.funnyzak.biz.service.open;

import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.open.Connector;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/18 11:00 AM
 * @description ConnectorService
 */
public interface ConnectorService {
    Connector fetch(String secretId);

    /**
     * 获取所有连接器名称
     *
     * @return
     */
    List<NutMap> allNames();

    Cnd baseCnd();

    Cnd baseCnd(Cnd cnd);

    Cnd condition(Cnd cnd, User currentUser, String name, String secretId, Boolean enable);

    PageredData<Connector> pager(Integer page, Integer pageSize, Cnd cnd);

    List<Connector> list(Cnd cnd, Integer count);

    String generateSecretId();

    String generateSecretKey();

    Connector add(User currentUser, Connector info) throws Exception;

    Connector resetSecretKey(User currentUser, String secretId) throws Exception;

    Connector edit(User currentUser, Connector info) throws Exception;

    void remove(User currentUser, String secretId) throws Exception;

    List<Connector> setListInfo(List<Connector> list, Boolean isSetAddUser, Boolean isSetUpdateUser);

    List<Connector> setListInfo(List<Connector> list);

    <T> List<T> setListConnectorInfo(List<T> list) throws Exception;

    <T> List<T> setListConnectorInfo(List<T> list, String getIdMethodName, String setInfoMethodName) throws Exception;

    /**
     * 根据ID列表获取连接器
     *
     * @param ids
     * @return
     */
    List<NutMap> findNamesByIds(List<String> ids);
}