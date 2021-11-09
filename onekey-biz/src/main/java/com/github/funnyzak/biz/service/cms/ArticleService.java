package com.github.funnyzak.biz.service.cms;

import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.acl.User;
import com.github.funnyzak.bean.cms.Article;
import com.github.funnyzak.bean.cms.enums.ArticleType;

import java.util.List;

public interface ArticleService {
    Cnd baseCnd(Cnd cnd);

    Integer count(Cnd cnd);

    Article fetch(String num);

    Article fetch(Long id);

    Article fetch(ArticleType type, Long relationId);

    Article add(Article info, User user) throws Exception;

    Article edit(Article info, User user) throws Exception;

    void remove(Long id) throws Exception;

    PageredData<Article> pager(Integer page, Integer pageSize, Cnd cnd, String orderBy, Boolean desc);

    List<Article> list(Cnd cnd);

    Cnd condition(Cnd cnd, User user, List<String> typeList,Long cateId, Long addTimeStart, Long addTimeEnd, String keyword, String author, String source, Boolean published);

    List<Article> setListInfo(List<Article> list, Boolean isSetAddUser);

}
