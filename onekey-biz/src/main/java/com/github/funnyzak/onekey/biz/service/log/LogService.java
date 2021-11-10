package com.github.funnyzak.onekey.biz.service.log;

import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.onekey.bean.acl.User;
import com.github.funnyzak.onekey.bean.log.OperationLog;
import com.github.funnyzak.onekey.biz.service.acl.UserService;
import com.github.funnyzak.onekey.common.utils.PUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/4/24 8:09 下午
 * @description LogService
 */
@Service
public class LogService {
    private final UserService userService;
    private final OperationLogService operationLogService;

    @Autowired
    public LogService( UserService userService
            , OperationLogService operationLogService) {
        this.userService = userService;
        this.operationLogService = operationLogService;
    }

    public PageredData<OperationLog> pager(int page, int pageSize, Cnd cnd) {
        return operationLogService.searchByPage(page, pageSize, cnd.desc("id"));
    }

    public Cnd condition(Cnd cnd, User user,String key) {
        return PUtils.cndBySearchKey(cnd, key, "ip", "module", "action", "description");
    }

    public List<OperationLog> setList(List<OperationLog> list, Boolean isSetAddUser) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (isSetAddUser != null && isSetAddUser) {
            list = userService.setListAddUserInfo(list);
        }
        return list;
    }
}