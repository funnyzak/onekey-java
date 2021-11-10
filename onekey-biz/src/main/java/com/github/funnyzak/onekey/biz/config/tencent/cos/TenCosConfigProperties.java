package com.github.funnyzak.onekey.biz.config.tencent.cos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/16 5:44 下午
 * @description CosConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ten-cloud-cos")
public class TenCosConfigProperties {
    private String secretId;

    private String secretKey;

    private String region;

    private String bucket;

    private String prefixKey;

    private String domain;
}