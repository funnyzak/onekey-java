package com.github.funnyzak.biz.service.log;

import com.github.funnyzak.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import com.github.funnyzak.bean.log.PmLog;
import com.github.funnyzak.bean.log.enums.SmsServerType;
import com.github.funnyzak.bean.log.enums.PmType;
import com.github.funnyzak.bean.log.enums.PmUse;
import com.github.funnyzak.common.utils.PUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/5/12 12:52 下午
 * @description PmLogService
 */
@Service
public class PmLogService extends GeneralService<PmLog> {
    public PageredData<PmLog> pager(int page, int pageSize, Cnd cnd) {
        return searchByPage(page, pageSize, cnd.desc("id"));
    }

    public List<PmLog> setList(List<PmLog> list, Boolean isSetLocation) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (isSetLocation != null && isSetLocation) {
            super.setLocationByIp(list);
        }
        return list;
    }

    public Cnd condition(Cnd cnd, List<Long> userIds, PmType pmType
            , String receive, PmUse use, Long addTimeStart, Long addTimeEnd
            , Boolean isSuccess, Boolean isVerify, SmsServerType server, String ip) {
        cnd = PUtils.cndBySearchKey(cnd, receive, "receive");
        return cnd.andEX("userId", "in", userIds)
                .andEX("type", "=", pmType)
                .andEX("use", "=", use)
                .andEX("server", "=", server)
                .andEX("addTime", ">=", addTimeStart)
                .andEX("addTime", "<=", addTimeEnd)
                .andEX("success", "=", isSuccess)
                .andEX("verify", "=", isVerify)
                .andEX("ip", "=", ip);
    }
}