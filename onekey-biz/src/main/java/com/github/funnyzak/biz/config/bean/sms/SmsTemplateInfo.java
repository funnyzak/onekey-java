package com.github.funnyzak.biz.config.bean.sms;

import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/13 6:31 PM
 * @description SmsBusiness
 */
@Data
public class SmsTemplateInfo {
    /**
     * 短信名称
     */
    private String name;

    /**
     * 短信签名
     */
    private String sign;

    /**
     * 作用
     */
    private PmUseType use;

    /**
     * 模板ID
     */
    private String tplId;

    /**
     * 模板内容
     */
    private String tplContent;
}