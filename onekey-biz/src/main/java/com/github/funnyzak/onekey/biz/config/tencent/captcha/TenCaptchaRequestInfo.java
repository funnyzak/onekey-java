package com.github.funnyzak.onekey.biz.config.tencent.captcha;

import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/12 1:20 PM
 * @description TenCaptchaRequestInfo
 */
@Data
public class TenCaptchaRequestInfo {
    /**
     * 公共参数，本接口取值：DescribeCaptchaResult。
     */
    private String action = "DescribeCaptchaResult";

    /**
     * 公共参数，本接口取值：2019-07-22。
     */
    private String version = "2019-07-22";

    /**
     * 公共参数，本接口不需要传递此参数。
     */
    private String region = "";

    /**
     * 固定填值：9。可在控制台配置不同验证码类型。
     */
    private Long captchaType = 9L;

    /**
     * 前端回调函数返回的用户验证票据
     */
    private String ticket;

    /**
     * 透传业务侧获取到的验证码使用者的IP
     */
    private String userIp;

    /**
     * 前端回调函数返回的随机字符串
     */
    private String randStr;

    /**
     * 业务 ID，网站或应用在多个业务中使用此服务，通过此 ID 区分统计数据
     */
    private Long businessId;

    /**
     * 场景 ID，网站或应用的业务下有多个场景使用此服务，通过此 ID 区分统计数据
     */
    private Long sceneId;

    /**
     * mac 地址或设备唯一标识
     */
    private String macAddress;

    /**
     * 手机设备号
     */
    private String imei;

    /**
     * 是否返回前端获取验证码时间，取值1：需要返回
     */
    private Long needGetCaptchaTime;
}