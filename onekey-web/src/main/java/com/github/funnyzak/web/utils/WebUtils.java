package com.github.funnyzak.web.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/21 4:34 下午
 * @description WebUtils
 */
public class WebUtils {
    public static String httpServletRequestToString(HttpServletRequest request) {
        if (request == null) return null;

        StringBuilder sb = new StringBuilder();

        sb.append("Request Method = [" + request.getMethod() + "], ");
        sb.append("Request URL Path = [" + request.getRequestURL() + "], ");

        String headers =
                Collections.list(request.getHeaderNames()).stream()
                        .map(headerName -> headerName + " : " + Collections.list(request.getHeaders(headerName)))
                        .collect(Collectors.joining(", "));

        if (headers.isEmpty()) {
            sb.append("Request headers: NONE,");
        } else {
            sb.append("Request headers: [" + headers + "],");
        }

        String parameters =
                Collections.list(request.getParameterNames()).stream()
                        .map(p -> p + " : " + Arrays.asList(request.getParameterValues(p)))
                        .collect(Collectors.joining(", "));

        if (parameters.isEmpty()) {
            sb.append("Request parameters: NONE.");
        } else {
            sb.append("Request parameters: [" + parameters + "].");
        }

        return sb.toString();
    }

    public static HttpServletRequest request() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        return sra.getRequest();
    }
}