package com.github.funnyzak.biz.service.open.impl;

import com.github.funnyzak.biz.service.GeneralService;
import org.nutz.dao.Cnd;
import com.github.funnyzak.bean.open.TToken;
import com.github.funnyzak.biz.service.open.TTokenService;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 9:49 上午
 * @description PTokenServiceImpl
 */
@Service
public class PTokenServiceImpl extends GeneralService<TToken> implements TTokenService {
    @Override
    public TToken token(String token) {
        return fetch(Cnd.NEW().andEX("token", "=", token));
    }

    @Override
    public TToken token(String csId, String appId, Long relationId) {
        return fetch(Cnd.NEW().andEX("csId", "=", csId).andEX("appId", "=", appId).andEX("relationId", "=", relationId));
    }

    @Override
    public void remove(String token) {
        super.delete(token);
    }

    @Override
    public TToken fetchOrCreate(String csId, String appId, Long relationId, boolean updateToken) {
        TToken token = token(csId, appId, relationId);
        if (token != null) {
            if (updateToken) {
                token.setUpdateTime(DateUtils.getTS());
                token.setToken(StringUtils.getUUIDNumberOnly());
                super.update(token);
            }
            return token;
        }

        token = new TToken();
        token.setCsId(csId);
        token.setAppId(appId);
        token.setToken(StringUtils.getUUIDNumberOnly());
        token.setRelationId(relationId);
        token.setAddTime(DateUtils.getTS());
        token.setExpireTime(DateUtils.getTS() + 3600 * 24 * 365);
        return super.save(token);
    }

    @Override
    public void updateToken(TToken token) {
        super.update(token);
    }
}