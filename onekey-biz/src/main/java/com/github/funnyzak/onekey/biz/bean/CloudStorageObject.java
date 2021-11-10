package com.github.funnyzak.onekey.biz.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/8 11:23 AM
 * @description CloudStorageObject
 */
@Data
@AllArgsConstructor
public class CloudStorageObject {
    private String domain;

    private String key;

    private String bucket;

    private String region;

    public String getUrl() {
        return this.domain + this.key;
    }
}