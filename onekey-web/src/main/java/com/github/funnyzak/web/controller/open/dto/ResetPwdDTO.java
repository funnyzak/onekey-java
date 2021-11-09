package com.github.funnyzak.web.controller.open.dto;

import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/6/8 6:46 下午
 * @description ResetPwdDTO
 */
@Data
public class ResetPwdDTO {
    private String oldPwd;

    private String newPwd;
}