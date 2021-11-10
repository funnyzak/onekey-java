package com.github.funnyzak.onekey.web.annotation.weblog;

import java.lang.annotation.*;

/**
 * @author Leon Yang
 * @date 2019/07/29
 *
 * 控制器日志处理
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebLogger {
    /**
     * 接口名称
     */
    String name() default "";

    /**
     * 日志是否入库
     */
    boolean intoDb() default false;
}