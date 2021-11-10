package com.github.funnyzak.onekey.web.hanlder;

import com.github.funnyzak.onekey.bean.member.MemberInfo;
import com.github.funnyzak.onekey.bean.open.Connector;
import com.github.funnyzak.onekey.bean.open.TToken;
import com.github.funnyzak.onekey.biz.dto.open.OpenRequestDTO;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.biz.service.CacheService;
import com.github.funnyzak.onekey.biz.service.open.OpenService;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import com.github.funnyzak.onekey.common.utils.TypeParse;
import com.github.funnyzak.onekey.web.annotation.auth.OpenMemberAuth;
import com.github.funnyzak.onekey.web.constants.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @description:Token验证过滤器,判断是否已登录
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private OpenService openService;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 判断接口是否需要登录
        OpenMemberAuth methodAnnotation = method.getAnnotation(OpenMemberAuth.class);

        // 有 @OpenMemberAuth 注解，需要认证
        if (methodAnnotation != null) {
            OpenRequestDTO oReq = new OpenRequestDTO(request);
            request.setAttribute(WebConstants.CURRENT_OPEN_REQUEST_NAME, oReq);

            Connector connector = cacheService.connector(oReq.getSecretId());

            if (null == connector) {
                throw new BizException("连接器不存在");
            }

            // 当前连接器@CurrentConnector
            request.setAttribute(WebConstants.CURRENT_CONNECTOR_NAME, connector);

            int rltCode = openService.auth(connector, oReq.getAppSign(), oReq.getUrl(), oReq.getIp(), oReq.getTs(), methodAnnotation.permissions(), methodAnnotation.logical());
            if (10000 != rltCode) {
                throw new BizException(openService.codeMsg(rltCode));
            }

            // 判断是否存在令牌信息，如果不存在，则提示登陆
            if (methodAnnotation.mustLogin() && StringUtils.isNullOrEmpty(oReq.getAuthToken())) {
                response.setStatus(401);
                throw new BizException("请登录后操作");
            }

            if (!StringUtils.isNullOrEmpty(oReq.getAuthToken())) {
                TToken memberToken = cacheService.token(oReq.getAuthToken());
                if (methodAnnotation.mustLogin()
                        && (memberToken == null
                        || (memberToken.getCsId() != null && !memberToken.getCsId().equals(oReq.getSecretId()))
                        || memberToken.getExpireTime() <= DateUtils.getTS())
                ) {
                    response.setStatus(401);
                    throw new BizException("Token无效");
                }

                if (memberToken != null) {
                    // 当前登录用户@CurrentMemberToken
                    request.setAttribute(WebConstants.CURRENT_MEMBER_TOKEN_NAME, memberToken);

                    MemberInfo member = cacheService.memberById(TypeParse.parseLong(memberToken.getRelationId()));

                    if (methodAnnotation.mustLogin() && member == null) {
                        response.setStatus(401);
                        throw new BizException("会员不存在");
                    }

                    if (methodAnnotation.mustLogin() && member.getAppId() != null && !member.getAppId().equals(oReq.getAppId())) {
                        response.setStatus(401);
                        throw new BizException("用户信息对当前APP无效");
                    }

                    if (methodAnnotation.mustLogin() && member.getLocked()) {
                        response.setStatus(401);
                        throw new BizException("用户已被锁定");
                    }
                    // 当前登录用户@CurrentMember
                    request.setAttribute(WebConstants.CURRENT_MEMBER_NAME, member);
                }
            }
        }
        return true;

    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest
                                   httpServletRequest, HttpServletResponse httpServletResponse, Object
                                   o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest
                                        httpServletRequest, HttpServletResponse httpServletResponse, Object
                                        o, Exception e) throws Exception {

    }
}