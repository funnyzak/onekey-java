package com.github.funnyzak.biz.ext.shiro;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Description:shiro框架 自定义session获取方式
 * 可自定义session获取规则。这里采用ajax请求头authToken携带sessionId的方式
 **/
public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final String AUTHORIZATION = "X-Auth-Token";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public ShiroSessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String sid = getSessionIdHeaderValue(request, response);
        if (StringUtils.isEmpty(sid)) {
            //如果没有携带id参数则按照父类的方式在cookie进行获取
            return super.getSessionId(request, response);
        } else {
            //如果请求头中有 authToken 则其值为sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sid);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sid;
        }
    }

    // 请求头中获取 sessionId 并把sessionId 放入 response 中
    private String getSessionIdHeaderValue(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            return null;
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String sessionId = httpRequest.getHeader(AUTHORIZATION);

            if (sessionId == null || StringUtils.isEmpty(sessionId)) {
                sessionId = httpRequest.getParameter(AUTHORIZATION);
            }

            // 每次读取之后 都把当前的 sessionId 放入 response 中
            if (!StringUtils.isEmpty(sessionId)) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setHeader(AUTHORIZATION, sessionId);
            }
            return sessionId;
        }
    }
}