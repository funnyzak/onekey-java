package com.github.funnyzak.web.hanlder;

import com.github.funnyzak.biz.dto.open.OpenRequestDTO;
import com.github.funnyzak.web.annotation.auth.CurrentOpenRequest;
import com.github.funnyzak.web.constants.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @description:自定义解析器实现参数绑定 增加方法注入，将含有 @CurrentOpenRequest 注解的方法参数注入当前登录用户
 */
public class CurrentOpenRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(OpenRequestDTO.class)
                && parameter.hasParameterAnnotation(CurrentOpenRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        OpenRequestDTO info = (OpenRequestDTO) webRequest.getAttribute(WebConstants.CURRENT_OPEN_REQUEST_NAME, RequestAttributes.SCOPE_REQUEST);
        if (info != null) {
            return info;
        }

        if (parameter.getParameterAnnotation(CurrentOpenRequest.class).required())
            throw new MissingServletRequestPartException(WebConstants.CURRENT_OPEN_REQUEST_NAME);

        return null;
    }
}