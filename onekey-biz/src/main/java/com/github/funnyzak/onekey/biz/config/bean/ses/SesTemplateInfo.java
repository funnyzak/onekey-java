package com.github.funnyzak.onekey.biz.config.bean.ses;

import lombok.Data;
import com.github.funnyzak.onekey.biz.config.bean.enums.PmUseType;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/17 11:30 AM
 * @description 邮件模板
 */
@Data
public class SesTemplateInfo {
    private String name;

    /**
     * 作用
     */
    private PmUseType use;

    private String subject;

    private String tplId;

    private String tplName;

    private String tplContent;

    private String fromEmailAddress;

    private String replayToAddress;
}