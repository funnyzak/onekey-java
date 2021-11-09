package com.github.funnyzak.biz.service.open;

import com.github.funnyzak.bean.open.TToken;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/15 9:49 上午
 * @description PTokenService
 */
public interface TTokenService {
    TToken token(String token);

    TToken token(String csId, String appId, Long relationId);

    TToken fetchOrCreate(String csId, String appId, Long relationId, boolean updateToken);

    void updateToken(TToken token);

    void remove(String token);
}