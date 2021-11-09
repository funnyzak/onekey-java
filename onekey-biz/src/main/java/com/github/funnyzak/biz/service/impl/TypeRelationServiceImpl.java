package com.github.funnyzak.biz.service.impl;

import com.github.funnyzak.biz.service.GeneralService;
import com.github.funnyzak.biz.service.TypeRelationService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.enums.TypeRelationEnums;
import com.github.funnyzak.bean.relation.TypeRelation;
import com.github.funnyzak.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/2/3 4:22 下午
 * @description CollectionRelationService
 */
@Service
public class TypeRelationServiceImpl extends GeneralService<TypeRelation> implements TypeRelationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TypeRelationServiceImpl() {
    }

    @Override
    public <T> PageredData<T> pagerByRelation(String listSqlName, String countSqlName, Class<T> tClass, TypeRelationEnums relationType, Cnd condition, Long typeId, Integer pageNumber, Integer pageSize) {
        List<T> list = listByCondition(listSqlName, tClass, relationType, condition, typeId, (pageNumber - 1) * pageSize, pageSize);

        Integer recordCount = countByRelation(countSqlName, condition, tClass, relationType, typeId);
        PageredData<T> pager = new PageredData<>();

        Pager page = new Pager();
        page.setRecordCount(recordCount);
        page.setPageSize(pageSize);
        page.setPageNumber(pageNumber);

        pager.setPager(page);
        pager.setDataList(list);
        return pager;
    }

    @Override
    public <T> List<T> listByCondition(String sqlName, Class<T> tClass, TypeRelationEnums relationType, Cnd condition, Long typeId, Integer start, Integer count) {
        Sql sql = dao().sqls().create(sqlName)
                .setParam("relation_type", relationType)
                .setParam("relation_type_id", typeId)
                .setParam("start", start)
                .setParam("count", count)
                .setCondition(condition)
                .setEntity(dao().getEntity(tClass))
                .setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(tClass);
    }

    @Override
    public <T> Integer countByRelation(String sqlName, Condition cnd, Class<T> tClass, TypeRelationEnums relationType, Long relationTypeId) {
        Sql sql = dao().sqls().create(sqlName)
                .setParam("relation_type", relationType)
                .setParam("relation_type_id", relationTypeId)
                .setCondition(cnd)
                .setEntity(dao().getEntity(tClass))
                .setCallback(Sqls.callback.integer());
        dao().execute(sql);
        return sql.getInt();
    }

    @Override
    public Integer countByRelation(TypeRelationEnums relationType, Long typeId, Long relationId) {
        return super.count(Cnd.NEW().andEX("type", "=", relationType).andEX("typeId", "=", typeId).andEX("relationId", "=", relationId));
    }

    @Override
    public TypeRelation exist(TypeRelationEnums relationType, Long typeId, Long relationId) {
        return super.fetch(Cnd.NEW().andEX("type", "=", relationType).andEX("typeId", "=", typeId).andEX("relationId", "=", relationId));
    }

    @Override
    public <T> List<NutMap> nutMapByRelation(String sqlName, Condition cnd, Class<T> tClass, TypeRelationEnums relationType, Long relationTypeId) {
        Sql sql = dao().sqls().create(sqlName)
                .setParam("relation_type", relationType)
                .setParam("relation_type_id", relationTypeId)
                .setCondition(cnd)
                .setEntity(dao().getEntity(tClass))
                .setCallback(Sqls.callback.maps());
        dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @Override
    public Integer setRelation(TypeRelationEnums relationType, Long typeId, List<Long> relationIdList, Boolean reset) {
        if (relationType == null || typeId == null || relationIdList == null) {
            return 0;
        }

        if (reset) {
            removeRelation(relationType, typeId, null);
        }
        Integer passCount = 0;
        if (relationIdList != null && relationIdList.size() > 0) {
            for (Long relationId : relationIdList) {
                passCount = setRelation(relationType, typeId, relationId) ? 1 : 0;
            }
        }
        return passCount;
    }

    @Override
    public Boolean setRelation(TypeRelationEnums relationType, Long typeId, Long relationId) {
        try {
            save(new TypeRelation(relationId, relationType, typeId, DateUtils.getTS(), DateUtils.getTS()));
            return true;
        } catch (Exception ex) {
            logger.error("设置业务关系数据操作失败==>", ex);
            return false;
        }
    }

    @Override
    public Integer setRelation(TypeRelationEnums relationType, List<Long> typeIdList, Long relationId, Boolean reset) {
        if (relationType == null || relationId == null) {
            return 0;
        }

        if (reset) {
            removeRelation(relationType, null, relationId);
        }
        Integer passCount = 0;
        if (typeIdList != null && typeIdList.size() > 0) {
            for (Long typeId : typeIdList) {
                passCount = setRelation(relationType, typeId, relationId) ? 1 : 0;
            }
        }
        return passCount;
    }

    @Override
    public List<Long> relationIdListByType(TypeRelationEnums relationType, Long typeId) {
        List<TypeRelation> list = query(Cnd.where("typeId", "=", typeId).and("type", "=", relationType));
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.stream().map(v -> v.getRelationId()).collect(Collectors.toList());
    }

    @Override
    public boolean removeRelation(TypeRelationEnums relationType, Long typeId, Long relationId) {
        return dao().clear(TypeRelation.class, Cnd.NEW().andEX("typeId", "=", typeId)
                .andEX("type", "=", relationType)
                .andEX("relationId", "=", relationId)) > 0;
    }
}