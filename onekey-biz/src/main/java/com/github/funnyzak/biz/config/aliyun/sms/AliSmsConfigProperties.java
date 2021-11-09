package com.github.funnyzak.biz.config.aliyun.sms;

import lombok.Data;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.config.bean.sms.SmsTemplateInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/7/22 11:40 AM
 * @description TenSmsConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ali-cloud-sms")
public class AliSmsConfigProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint = "dysmsapi.aliyuncs.com";

    /**
     * 短信签名
     */
    private String sign;

    /**
     * 短信模板集合
     */
    private List<SmsTemplateInfo> templates;

    public SmsTemplateInfo searchTemplate(PmUseType smsFunctionType) {
        return PmUseType.searchObjectByUse(this.templates, smsFunctionType);
    }
}