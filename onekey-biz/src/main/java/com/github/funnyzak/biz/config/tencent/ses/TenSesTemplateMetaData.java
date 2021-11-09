package com.github.funnyzak.biz.config.tencent.ses;

import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/6/16 2:22 PM
 * @description TesSesTemplatesMetaData
 */
@Data
public class TenSesTemplateMetaData {
    /**
     * 创建时间
     */
    private Long CreatedTimestamp;

    /**
     * 模板名称
     */
    private String TemplateName;

    /**
     * 模板状态 1-审核中|0-已通过|2-拒绝|其它-不可用
     */
    private Integer TemplateStatus;

    /**
     * 模板ID
     */
    private Integer TemplateID;

    /**
     * 审核原因
     */
    private String ReviewReason;
}