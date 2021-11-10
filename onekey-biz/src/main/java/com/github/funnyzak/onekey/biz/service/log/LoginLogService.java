package com.github.funnyzak.onekey.biz.service.log;

import com.github.funnyzak.onekey.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.log.LoginLog;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.common.utils.PUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LoginLogService extends GeneralService<LoginLog> {
    private final UserService userService;

    @Autowired
    public LoginLogService(UserService userService) {
        this.userService = userService;
    }

    public PageredData<LoginLog> pager(int page, int pageSize, Cnd cnd) {
        return searchByPage(page, pageSize, cnd.desc("id"));
    }

    public Cnd condition(Cnd cnd, String key) {
        return PUtils.cndBySearchKey(cnd, key, "ip");
    }

    public List<LoginLog> setList(List<LoginLog> list, Boolean isSetAddUser, Boolean isSetLocation) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (isSetAddUser != null && isSetAddUser) {
            list = userService.setListAddUserInfo(list);
        }

        if (isSetLocation != null && isSetLocation) {
            list = setLocationByIp(list);
        }
        return list;
    }
}
