package com.github.funnyzak.web.hanlder;

import org.apache.shiro.authz.UnauthenticatedException;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.common.Result;
import com.github.funnyzak.web.config.common.ProfileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * @author potato
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ProfileConfig profileConfig;

    @ExceptionHandler(value = Exception.class)
    public Result defaultErrorHandler(HttpServletResponse response, Exception e) throws Exception {
        logger.error("error=>", e);
        if (e instanceof UnauthenticatedException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return Result.fail("没有权限进行此操作!");
        }
        return profileConfig.getActiveProfile().equals(ProfileConfig.DEV_PROFILE) || e instanceof BizException ? Result.exception(e) : Result.fail("发生错误");
    }
}
