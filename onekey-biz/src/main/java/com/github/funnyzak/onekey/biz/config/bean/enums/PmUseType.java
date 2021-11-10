package com.github.funnyzak.onekey.biz.config.bean.enums;

import com.github.funnyzak.onekey.bean.log.enums.PmUse;
import com.github.funnyzak.onekey.common.utils.PUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/13 6:35 PM
 * @description SmsBusinessFunction
 */
public enum PmUseType {
    REGISTER_SUCCESS_NOTICE("注册成功通知", PmUse.NOTIFY),
    LOGIN_VERIFY("登陆验证码", PmUse.LOGIN),
    REGISTER_VERIFY("注册验证码", PmUse.REGISTER),
    RESET_PASSWORD_VERIFY("重设密码验证码", PmUse.FORGET_PASSWORD),
    COMMON_VERIFY("通用验证码", PmUse.VERIFY_ACTION);

    PmUseType(String name, PmUse belong) {
        this.name = name;
        this.belong = belong;
    }

    private PmUse belong;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PmUse getBelong() {
        return belong;
    }

    public void setBelong(PmUse belong) {
        this.belong = belong;
    }

    public static <T> T searchObjectByUse(List<T> objects, PmUseType pmUseType) {
        if (objects == null || objects.size() == 0) {
            return null;
        }

        List<T> findTpl = objects.stream().filter(v -> PUtils.columnValue(v, "getUse") != null && PUtils.columnValue(v, "getUse").equals(pmUseType)).collect(Collectors.toList());
        return findTpl.size() == 0 ? null : findTpl.get(0);
    }
}
