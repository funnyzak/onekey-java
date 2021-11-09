package com.github.funnyzak.biz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.enums.TypeRelationEnums;
import com.github.funnyzak.bean.relation.TypeRelation;

import java.util.List;

public interface TypeRelationService {

    /**
     * 分页数据
     */
    <T> PageredData<T> pagerByRelation(String listSqlName, String countSqlName, Class<T> tClass, TypeRelationEnums relationType, Cnd condition, Long typeId, Integer pageNumber, Integer pageSize);

    <T> List<T> listByCondition(String sqlName, Class<T> tClass, TypeRelationEnums relationType, Cnd condition, Long typeId, Integer start, Integer count);

    <T> Integer countByRelation(String sqlName, Condition cnd, Class<T> tClass, TypeRelationEnums relationType, Long relationTypeId);

    Integer countByRelation(TypeRelationEnums relationType, Long typeId, Long relationId);

    TypeRelation exist(TypeRelationEnums relationType, Long typeId, Long relationId);

    <T> List<NutMap> nutMapByRelation(String sqlName, Condition cnd, Class<T> tClass, TypeRelationEnums relationType, Long relationTypeId);

    /**
     * 根据类型获取所有的业务ID列表
     */
    List<Long> relationIdListByType(TypeRelationEnums relationType, Long typeId);

    /**
     * 移除关联
     *
     * @param relationType 业务类型
     * @param typeId       业务ID
     * @param relationId   藏品ID
     */
    boolean removeRelation(TypeRelationEnums relationType, Long typeId, Long relationId);

    /**
     * 设置业务关联
     *
     * @param relationType   业务类型
     * @param typeId         业务类型ID
     * @param relationIdList 业务对应内容ID列表
     * @param reset          是否删除之前的关联信息
     * @return
     */
    Integer setRelation(TypeRelationEnums relationType, Long typeId, List<Long> relationIdList, Boolean reset);

    Integer setRelation(TypeRelationEnums relationType, List<Long> typeIdList, Long relationId, Boolean reset);

    /**
     * 设置业务关联
     */
    Boolean setRelation(TypeRelationEnums relationType, Long typeId, Long relationId);
}
