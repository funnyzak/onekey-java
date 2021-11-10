package com.github.funnyzak.onekey.web.controller.open.dto;

import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/6/8 3:09 下午
 * @description MemberPhoneRegisterDTO
 */
@Data
public class MemberPhoneRegisterDTO {
    private String receive;

    private String idNum;

    private String verifyCode;

    private String pwd;
}