package com.github.funnyzak.onekey.biz.core.lib;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/26 10:42 上午
 * @description CheckObject
 */
public interface CheckObject {
    /**
     * Return true if the object is correctly initialized or false if not.
     * @return Object initialization flag status.
     * @since v0.1.0
     */
    boolean isCorrectlyInitialized();
}