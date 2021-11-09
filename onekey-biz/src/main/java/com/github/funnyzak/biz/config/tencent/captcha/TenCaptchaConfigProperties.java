package com.github.funnyzak.biz.config.tencent.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/5/11 5:44 下午
 * @description CosConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ten-cloud-captcha")
public class TenCaptchaConfigProperties {
    private String secretId;

    private String secretKey;

    private String endpoint;

    private String sdkAppId;

    private String sdkAppSecret;
}