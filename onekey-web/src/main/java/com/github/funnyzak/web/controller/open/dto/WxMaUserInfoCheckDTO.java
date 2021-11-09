package com.github.funnyzak.web.controller.open.dto;

import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/10/20 6:14 下午
 * @description WxMaUserInfoCheckDTO
 */
@Data
public class WxMaUserInfoCheckDTO {
    private String sessionKey;

    private String rawData;

    private String signature;

    private String encryptedData;

    private String iv;
}