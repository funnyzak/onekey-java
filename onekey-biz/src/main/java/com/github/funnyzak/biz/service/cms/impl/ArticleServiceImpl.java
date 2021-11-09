package com.github.funnyzak.biz.service.cms.impl;

import com.github.funnyzak.biz.constant.BizConstants;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.cms.Article;
import com.github.funnyzak.bean.cms.enums.ArticleType;
import com.github.funnyzak.biz.service.acl.UserService;
import com.github.funnyzak.biz.service.cms.ArticleService;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.PUtils;
import com.github.funnyzak.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/6/11 5:47 下午
 * @description ArticleService
 */
@Service
public class ArticleServiceImpl extends GeneralService<Article> implements ArticleService {
    private final UserService userService;

    @Autowired
    public ArticleServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Cnd baseCnd(Cnd cnd) {
        return (cnd == null ? Cnd.NEW() : cnd).and("del", "=", false);
    }

    private Cnd baseCnd() {
        return baseCnd(null);
    }

    @Override
    public Integer count(Cnd cnd) {
        return super.count(cnd);
    }

    @Override
    public Article fetch(String num) {
        return super.fetch(baseCnd().andEX("num", "=", num));
    }

    @Override
    public Article fetch(Long id) {
        return super.fetch(baseCnd().andEX("id", "=", id));
    }

    @Override
    public Article fetch(ArticleType type, Long relationId) {
        return super.fetch(baseCnd().andEX("type", "=", type).andEX("relationId", "=", relationId));
    }

    @Override
    public Article add(Article info, User user) throws Exception {
        info.setAddTime(DateUtils.getTS());
        info.setAddUserId(user.getId());
        info.setDel(false);
        addOperationLog(user, BizConstants.CmsConst.NAME, String.format("创建%s", info.getType().getName()), info);
        return save(info);
    }

    @Override
    public Article edit(Article info, User user) throws Exception {
        info.setUpdateTime(DateUtils.getTS());
        info.setUpdateUserId(user.getId());
        info.setDel(false);
        if (!super.update(info, BizConstants.CmsConst.ARTICLE_CAN_EDIT_COLUMN_NAME_LIST.split(","))) {
            throw new BizException("编辑失败");
        }
        addOperationLog(user, BizConstants.CmsConst.NAME, String.format("编辑%s", info.getType().getName()), info);
        update(info);
        return info;
    }

    @Override
    public void remove(Long id) throws Exception {
        Article info = fetch(id);
        if (info == null) {
            throw new BizException("不存在");
        }
        info.setDel(true);
        super.update(info);
    }

    @Override
    public PageredData<Article> pager(Integer page, Integer pageSize, Cnd cnd, String orderBy, Boolean desc) {
        PageredData<Article> pager = super.searchByPage(page, pageSize, (cnd == null ? Cnd.NEW() : cnd).orderBy(StringUtils.isNullOrEmpty(orderBy) ? "id" : orderBy, orderBy == null || desc ? "desc" : "asc"));
        pager.setDataList(setListInfo(pager.getDataList(), true));
        return pager;
    }

    @Override
    public List<Article> list(Cnd cnd) {
        PageredData<Article> pager = super.searchByPage(1, 100000, cnd);
        pager.setDataList(setListInfo(pager.getDataList(), true));
        return pager.getDataList();
    }

    @Override
    public Cnd condition(Cnd cnd, User user, List<String> typeList, Long cateId, Long addTimeStart, Long addTimeEnd, String keyword, String author, String source, Boolean published) {
        cnd = PUtils.cndBySearchKey(cnd, keyword, "title", "subTitle");
        cnd = PUtils.cndBySearchKey(cnd, author, "author");
        cnd = PUtils.cndBySearchKey(cnd, source, "source");
        cnd.andEX("type", "in", typeList)
                .andEX("addTime", ">=", addTimeStart)
                .andEX("cateId", "=", cateId)
                .andEX("addTime", "<=", addTimeEnd)
                .andEX("published", "=", published);
        return cnd;
    }

    @Override
    public List<Article> setListInfo(List<Article> list, Boolean isSetAddUser) {
        if (list == null || list.size() == 0) {
            return null;
        }
        if (isSetAddUser != null && isSetAddUser) {
            userService.setListAddUserInfo(list);
        }
        return list;
    }
}