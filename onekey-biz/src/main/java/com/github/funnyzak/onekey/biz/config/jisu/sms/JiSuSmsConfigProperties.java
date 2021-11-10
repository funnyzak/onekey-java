package com.github.funnyzak.onekey.biz.config.jisu.sms;

import lombok.Data;
import com.github.funnyzak.onekey.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.onekey.biz.config.bean.sms.SmsTemplateInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/24 11:32 AM
 * @description JiSuSmsConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ji-su-api.sms")
public class JiSuSmsConfigProperties {
    private String appKey;

    private String endpoint;

    private List<SmsTemplateInfo> templates;

    public SmsTemplateInfo searchTemplate(PmUseType smsFunctionType) {
        return PmUseType.searchObjectByUse(this.templates, smsFunctionType);
    }
}