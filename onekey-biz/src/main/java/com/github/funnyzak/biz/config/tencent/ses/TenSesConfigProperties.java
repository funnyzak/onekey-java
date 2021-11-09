package com.github.funnyzak.biz.config.tencent.ses;

import lombok.Data;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.config.bean.ses.SesTemplateInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/11 6:46 PM
 * @description TenSesConfigProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "ten-cloud-ses")
public class TenSesConfigProperties {
    private String secretId;

    private String secretKey;

    private String endpoint;

    private String region;

    private String fromEmailAddress;

    private String replayToAddress;

    private List<SesTemplateInfo> templates;

    public SesTemplateInfo searchTemplate(PmUseType pmUseType) {
        return PmUseType.searchObjectByUse(this.templates, pmUseType);
    }
}