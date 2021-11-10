package com.github.funnyzak.onekey.biz.service;

import com.github.funnyzak.onekey.bean.member.MemberInfo;
import com.github.funnyzak.onekey.bean.open.Connector;
import com.github.funnyzak.onekey.bean.open.TToken;
import com.github.funnyzak.onekey.biz.constant.RedisConstants;
import com.github.funnyzak.onekey.biz.service.member.MemberService;
import com.github.funnyzak.onekey.biz.service.open.ConnectorService;
import com.github.funnyzak.onekey.biz.service.open.TTokenService;
import com.github.funnyzak.onekey.common.db.RedisOp;
import com.github.funnyzak.onekey.common.utils.PUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/16 10:33 上午
 * @description OpenCache
 */
@Service
@DependsOn("redisConfig")
@ConditionalOnClass(RedisOp.class)
public class CacheService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConnectorService connectorService;
    private TTokenService tokenService;
    private MemberService memberService;
    private RedisOp redisOp;

    @Autowired
    public CacheService(
            ConnectorService connectorService
            , MemberService memberService
            , @Autowired(required = false) RedisOp redisOp
            , TTokenService tokenService
    ) {
        this.connectorService = connectorService;
        this.tokenService = tokenService;
        this.memberService = memberService;
        this.redisOp = redisOp;
    }

    public Connector connector(String secretId) {
        if (StringUtils.isNullOrEmpty(secretId)) {
            return null;
        }
        return object(String.format(RedisConstants.Keys.CONNECTOR_KEY, secretId), RedisConstants.Expire.CONNECTOR_EXPIRE, connectorService, PUtils.getMethod(connectorService, "fetch", String.class), secretId);
    }

    public MemberInfo member(@NotNull String app, String dbKey, String dbVal) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, dbKey, dbVal), memberService, PUtils.getMethod(memberService, "findByNameValue", String.class, String.class, String.class), app, dbKey, dbVal);
    }

    public MemberInfo memberByPhone(@NotNull String app, String phone) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "phone", phone), memberService, PUtils.getMethod(memberService, "findByPhone", String.class, String.class), app, phone);
    }

    public MemberInfo memberByIdNum(@NotNull String app, String idNum) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "idNum", idNum), memberService, PUtils.getMethod(memberService, "findByIdNum", String.class, String.class), app, idNum);
    }

    public MemberInfo memberById(Long id) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, "p", "id", id), memberService, PUtils.getMethod(memberService, "fetch", Long.class), id);
    }

    public void removeByMemberId(Long id) {
        remove(String.format(RedisConstants.Keys.MEMBER_KEY, "p", "id", id));
    }

    public MemberInfo memberByUsername(@NotNull String app, String username) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "username", username), memberService, PUtils.getMethod(memberService, "findByUserName", String.class, String.class), app, username);
    }

    public MemberInfo memberByWxMa(@NotNull String app, String openId) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "wxMa", openId), memberService, PUtils.getMethod(memberService, "findByWxMa", String.class, String.class), app, openId);
    }

    public MemberInfo memberByWxMp(@NotNull String app, String openId) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "wxMp", openId), memberService, PUtils.getMethod(memberService, "findByWxMp", String.class, String.class), app, openId);
    }

    public MemberInfo memberByWxUnion(@NotNull String app, String unionId) {
        return object(String.format(RedisConstants.Keys.MEMBER_KEY, app, "wxUnion", unionId), memberService, PUtils.getMethod(memberService, "findByWxUnion", String.class, String.class), app, unionId);
    }

    public TToken token(String token) {
        return object(String.format(RedisConstants.Keys.T_TOKEN_KEY, token), RedisConstants.Expire.T_TOKEN_EXPIRE, tokenService, PUtils.getMethod(tokenService, "token", String.class), token);
    }

    public void removeToken(String token) {
        remove(String.format(RedisConstants.Keys.T_TOKEN_KEY, token));
    }

    public <T, Q> T object(String cacheKey, Q fetchObjService, Method fetchDbMethod, Object... methodParams) {
        return object(cacheKey, RedisConstants.Expire.KEY_EXPIRE, fetchObjService, fetchDbMethod, methodParams);
    }

    public <T, Q> T object(String cacheKey, Integer cacheExpire, Q fetchObjService, Method fetchDbMethod, Object... methodParams) {
        Object cacheObj = null;
        try {
            cacheObj = redisOp == null ? null : redisOp.get(cacheKey);
        } catch (Exception ex) {
            logger.error("从Redis缓存获取" + cacheKey + "失败==>", ex);
        }

        if (cacheObj == null && fetchDbMethod != null) {
            Object fetchObj = null;
            try {
                fetchObj = fetchDbMethod.invoke(fetchObjService, methodParams);
            } catch (Exception ex) {
                logger.error("从数据库查询数据对象失败==>", ex);
                return null;
            }
            if (fetchObj == null) {
                return null;
            }
            T t = (T) fetchObj;

            try {
                if (redisOp != null) redisOp.set(cacheKey, t, cacheExpire);
            } catch (Exception ex) {
                logger.error("从Redis设置缓存" + cacheKey + "失败==>", ex);
            }

            return t;
        }
        return cacheObj == null ? null : (T) cacheObj;
    }

    public void remove(String... cacheKeys) {
        try {
            redisOp.del(cacheKeys);
        } catch (Exception ex) {
            logger.error("从Redis缓存删除Keys:" + Arrays.toString(cacheKeys) + "失败==>", ex);
        }
    }


    public void setObject(String cacheKey, Object object, Integer cacheExpire) {
        try {
            if (redisOp == null) {
                logger.error("当前Redis操作对象为空，无法缓存对象。key:{}, object:{}", cacheKey, object.toString());
                return;
            }
            redisOp.set(cacheKey, object, cacheExpire);
        } catch (Exception ex) {
            logger.error("Redis缓存对象失败，key:{}, object:{}, 错误信息==>", cacheKey, object.toString(), ex);
        }
    }
}