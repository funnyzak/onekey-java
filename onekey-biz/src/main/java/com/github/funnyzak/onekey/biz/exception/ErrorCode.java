package com.github.funnyzak.onekey.biz.exception;

/**
 * 错误接口定义
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getDescription();
}