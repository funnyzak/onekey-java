package com.github.funnyzak.biz.service;

import org.nutz.lang.util.NutMap;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.exception.BizException;

import java.util.List;
import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/13 10:25 AM
 * @description CloudSmsService
 */
public interface CloudSmsService {
    /**
     * 发送短信
     *
     * @param phoneNumList 接受手机号列表，格式按各平台要求
     * @param smsData      短信参数列表
     * @throws BizException
     */
    NutMap sendSms(List<String> phoneNumList, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception;

    /**
     * 发送短信
     *
     * @param phoneNum        手机号
     * @param smsFunctionType 短信类型
     * @param smsData         发送短信数据
     * @return
     * @throws Exception
     */
    NutMap sendSms(String phoneNum, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception;

    /**
     * 发送验证码
     *
     * @param phoneNum     手机号
     * @param code         数字验证码
     * @param expireMinute 验证码有效分钟数
     * @return
     * @throws Exception
     */
    NutMap sendVerifySms(String phoneNum, String code, Integer expireMinute) throws Exception;
}