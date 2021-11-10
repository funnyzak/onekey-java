package com.github.funnyzak.onekey.biz.config.aliyun.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/7 6:18 PM
 * @description AliOssConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ali-cloud-oss")
public class AliOssConfigProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String endPoint;

    private String bucketName;

    private String prefixKey;

    private String domain;
}