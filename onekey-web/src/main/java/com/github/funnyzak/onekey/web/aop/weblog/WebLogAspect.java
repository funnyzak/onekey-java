package com.github.funnyzak.onekey.web.aop.weblog;

import com.github.funnyzak.onekey.web.utils.WebUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.nutz.json.Json;
import com.github.funnyzak.onekey.bean.log.WebLog;
import com.github.funnyzak.onekey.biz.service.log.WebLogService;
import com.github.funnyzak.onekey.web.annotation.weblog.WebLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leon Yang
 * @date 2019/07/29
 * <p>
 * WebLog切面类
 */
@Aspect
@Component
@Order(77)
public class WebLogAspect {
    private static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    private final WebLogService webLogService;
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    private static final String START_TIME = "startTime";
    private static final String REQUEST_PARAMS = "requestParams";
    private static final String CONTROLLER_METHOD = "controllerMethodName";

    @Autowired
    public WebLogAspect(WebLogService webLogService) {
        this.webLogService = webLogService;
    }

    /**
     * 定义一个切点
     */
    @Pointcut("execution(* com.github.funnyzak.onekey.web.controller..*.*(..))")
    public void webLog() {
    }

    @Before(value = "webLog() && @annotation(controllerWebLog)")
    public void doBefore(JoinPoint joinPoint, WebLogger controllerWebLog) {
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, startTime);

        // 获得类名，方法名，参数和参数名称。
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argumentNames = methodSignature.getParameterNames();

        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            sb.append(argumentNames[i] + "->");
            sb.append(argument != null ? argument.toString() : "null ");
        }
        sb.append(")");

        threadInfo.put(REQUEST_PARAMS, sb.toString());
        threadInfo.put(CONTROLLER_METHOD, className + "." + methodName);
        threadLocal.set(threadInfo);

        logger.info("操作：{}, {}接口开始调用, 请求参数={}",
                controllerWebLog.name(),
                className + "." + methodName,
                threadInfo.get(REQUEST_PARAMS));
    }


    @AfterReturning(value = "webLog() && @annotation(controllerWebLog)", returning = "result")
    public void doAfterReturning(WebLogger controllerWebLog, Object result) {
        Map<String, Object> threadInfo = threadLocal.get();
        long takeTime = System.currentTimeMillis() - (long) threadInfo.getOrDefault(START_TIME, System.currentTimeMillis());

        if (controllerWebLog.intoDb()) {
            WebLog webLog = new WebLog();
            webLog.setControllerName(threadInfo.getOrDefault(CONTROLLER_METHOD, "").toString());
            webLog.setOperationName(controllerWebLog.name());
            webLog.setTakeTime(takeTime);
            webLog.setRequest(threadInfo.getOrDefault(REQUEST_PARAMS, "").toString());
            webLog.setResponse(Json.toJson(result));
            webLogService.save(webLog);
        }

        threadLocal.remove();

        logger.info("操作：{}, {}接口结束调用, 耗时={}ms, 请求参数={}, 返回数据={}。================> 原始信息：{}。",
                controllerWebLog.name(),
                threadInfo.getOrDefault(CONTROLLER_METHOD, "").toString(),
                takeTime,
                threadInfo.get(REQUEST_PARAMS),
                result,
                WebUtils.httpServletRequestToString(WebUtils.request()));
    }

    @AfterThrowing(value = "webLog()&& @annotation(controllerWebLog)", throwing = "throwable")
    public void doAfterThrowing(WebLogger controllerWebLog, Throwable throwable) {
        Map<String, Object> threadInfo = threadLocal.get();

        WebLog webLog = new WebLog();
        webLog.setError(true);
        webLog.setControllerName(threadInfo.getOrDefault(CONTROLLER_METHOD, "").toString());
        webLog.setOperationName(controllerWebLog.name());
        webLog.setRequest(threadInfo.getOrDefault(REQUEST_PARAMS, "").toString());
        webLog.setStackTrace(Json.toJson(throwable.getStackTrace().toString()));
        webLogService.save(webLog);

        threadLocal.remove();

        logger.error("操作：{}, {}接口调用异常, request={}, 异常信息={}",
                controllerWebLog.name(),
                threadInfo.getOrDefault(CONTROLLER_METHOD, "").toString(),
                threadInfo.get(REQUEST_PARAMS),
                throwable);
    }
}