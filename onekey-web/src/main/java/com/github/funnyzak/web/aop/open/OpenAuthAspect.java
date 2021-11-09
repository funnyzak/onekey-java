package com.github.funnyzak.web.aop.open;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.nutz.json.Json;
import com.github.funnyzak.bean.log.WebLog;
import com.github.funnyzak.bean.open.Connector;
import com.github.funnyzak.bean.open.ConnectorLog;
import com.github.funnyzak.biz.dto.open.OpenRequestDTO;
import com.github.funnyzak.biz.service.CacheService;
import com.github.funnyzak.biz.service.log.WebLogService;
import com.github.funnyzak.biz.service.open.ConnectorLogService;
import com.github.funnyzak.biz.service.open.OpenService;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.annotation.auth.OpenAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leon Yang
 * @date 2019/07/29
 * <p>
 * 开放API 切面类
 */
@Aspect
@Component
@Order(77)
public class OpenAuthAspect {
    private static Logger logger = LoggerFactory.getLogger(OpenAuthAspect.class);

    private final ConnectorLogService connectorLogService;
    private final CacheService openCacheService;
    private final WebLogService webLogService;
    private final OpenService openService;
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    private static final String START_TIME = "startTime";
    private static final String CONTROLLER_METHOD = "controllerMethodName";
    private static final String CONNECTOR_NAME = "connector";
    private static final String REQUEST_IP = "requestIp";
    private static final String AUTH_CODE = "authCode";

    @Autowired
    public OpenAuthAspect(
            ConnectorLogService connectorLogService
            , CacheService openCacheService
            , OpenService openService
            , WebLogService webLogService
    ) {
        this.connectorLogService = connectorLogService;
        this.openService = openService;
        this.openCacheService = openCacheService;
        this.webLogService = webLogService;
    }

    /**
     * 定义一个切点
     */
    @Pointcut("execution(* org.skyf.potato.web.controller..*.*(..))")
    public void auth() {
    }

    @AfterReturning(value = "auth() && @annotation(openAuth)", returning = "result")
    public void doAfterReturning(OpenAuth openAuth, Object result) {
        Map<String, Object> threadInfo = threadLocal.get();
        long takeTime = System.currentTimeMillis() - (long) threadInfo.getOrDefault(START_TIME, System.currentTimeMillis());

        if (threadInfo.get(AUTH_CODE).toString().equals("10000")) {
            ConnectorLog connectorLog = new ConnectorLog();
            connectorLog.setConnectorId(((Connector) threadInfo.get(CONNECTOR_NAME)).getId());
            connectorLog.setSystem(openAuth.system());
            connectorLog.setMethodName(threadInfo.get(CONTROLLER_METHOD).toString());
            connectorLog.setRequestTime((long) threadInfo.getOrDefault(START_TIME, System.currentTimeMillis()));
            connectorLog.setElapsedTime(takeTime);
            connectorLog.setIp(threadInfo.get(REQUEST_IP).toString());
            connectorLogService.save(connectorLog);
        }

        threadLocal.remove();
    }


    @Around(value = "auth()&& @annotation(openAuth)")
    public Object doAround(ProceedingJoinPoint joinPoint, OpenAuth openAuth) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        OpenRequestDTO oReq = new OpenRequestDTO(request);

        Connector connector = openCacheService.connector(oReq.getSecretId());

        /******记录共享信息*******/
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, System.currentTimeMillis());
        threadInfo.put(CONNECTOR_NAME, connector);
        threadInfo.put(REQUEST_IP, oReq.getIp());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        threadInfo.put(CONTROLLER_METHOD, className + "." + methodName);

        int rltCode = openService.auth(connector, oReq.getAppSign(), oReq.getUrl(), oReq.getIp(), oReq.getTs(), openAuth.permissions(), openAuth.logical());
        threadInfo.put(AUTH_CODE, rltCode);

        threadLocal.set(threadInfo);
        /*******结束共享信息******/

        if (10000 == rltCode) {
            return joinPoint.proceed();
        } else {
            return Result.fail(String.format(openService.codeMsg(rltCode)));
        }
    }

    @AfterThrowing(value = "auth()&& @annotation(openAuth)", throwing = "throwable")
    public void doAfterThrowing(OpenAuth openAuth, Throwable throwable) {
        Map<String, Object> threadInfo = threadLocal.get();
        WebLog webLog = new WebLog();
        webLog.setError(true);
        webLog.setControllerName(threadInfo.getOrDefault(CONTROLLER_METHOD, "").toString());
        webLog.setOperationName(openAuth.system());
        webLog.setRequest("未记录");
        webLog.setStackTrace(Json.toJson(throwable.getStackTrace().toString()));
        webLogService.save(webLog);

        threadLocal.remove();
    }
}