package com.github.funnyzak.onekey.web.hanlder;

import com.github.funnyzak.onekey.bean.open.Connector;
import com.github.funnyzak.onekey.web.annotation.auth.CurrentConnector;
import com.github.funnyzak.onekey.web.constants.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @description:自定义解析器实现参数绑定 增加方法注入，将含有 @CurrentConnector 注解的方法参数注入当前登录用户
 */
public class CurrentConnectorMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Connector.class)
                && parameter.hasParameterAnnotation(CurrentConnector.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Connector info = (Connector) webRequest.getAttribute(WebConstants.CURRENT_CONNECTOR_NAME, RequestAttributes.SCOPE_REQUEST);
        if (info != null) {
            return info;
        }
        if (parameter.getParameterAnnotation(CurrentConnector.class).required())
            throw new MissingServletRequestPartException(WebConstants.CURRENT_CONNECTOR_NAME);

        return null;
    }
}