package com.github.funnyzak.web.controller.open.dto;

import lombok.Data;
import com.github.funnyzak.bean.log.enums.PmType;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/6/8 3:07 下午
 * @description SendSmsDTO
 */
@Data
public class SendPmDTO {
    private String receive;

    private PmType type;

    private PmUseType use;
}