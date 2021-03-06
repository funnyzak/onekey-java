package com.github.funnyzak.onekey.web.hanlder;

import com.github.funnyzak.onekey.bean.member.MemberInfo;
import com.github.funnyzak.onekey.web.annotation.auth.CurrentMember;
import com.github.funnyzak.onekey.web.constants.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @description:自定义解析器实现参数绑定
 * 增加方法注入，将含有 @CurrentMember 注解的方法参数注入当前登录用户
 */
public class CurrentMemberMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(MemberInfo.class)
                && parameter.hasParameterAnnotation(CurrentMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MemberInfo member = (MemberInfo) webRequest.getAttribute(WebConstants.CURRENT_MEMBER_NAME, RequestAttributes.SCOPE_REQUEST);
        if (member != null) {
            return member;
        }

        if (parameter.getParameterAnnotation(CurrentMember.class).required())
            throw new MissingServletRequestPartException(WebConstants.CURRENT_MEMBER_NAME);

        return null;
    }
}