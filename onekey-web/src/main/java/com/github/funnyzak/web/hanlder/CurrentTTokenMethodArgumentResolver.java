package com.github.funnyzak.web.hanlder;

import com.github.funnyzak.bean.open.TToken;
import com.github.funnyzak.web.annotation.auth.CurrentTToken;
import com.github.funnyzak.web.constants.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @description:自定义解析器实现参数绑定
 * 增加方法注入，将含有 @CurrentMemberToken 注解的方法参数注入当前登录用户
 */
public class CurrentTTokenMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(TToken.class)
                && parameter.hasParameterAnnotation(CurrentTToken.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        TToken memberToken = (TToken) webRequest.getAttribute(WebConstants.CURRENT_MEMBER_TOKEN_NAME, RequestAttributes.SCOPE_REQUEST);
        if (memberToken != null) {
            return memberToken;
        }

        if (parameter.getParameterAnnotation(CurrentTToken.class).required())
            throw new MissingServletRequestPartException(WebConstants.CURRENT_MEMBER_TOKEN_NAME);

        return null;
    }
}