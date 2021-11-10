package com.github.funnyzak.onekey.biz.config.tencent.sms;

import lombok.Data;
import com.github.funnyzak.onekey.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.onekey.biz.config.bean.sms.SmsTemplateInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/11 6:46 PM
 * @description TenSmsConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ten-cloud-sms")
public class TenSmsConfigProperties {
    private String secretId;

    private String secretKey;

    private String endpoint;

    /**
     * 短信SDK AppId
     */
    private String sdkAppId;

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