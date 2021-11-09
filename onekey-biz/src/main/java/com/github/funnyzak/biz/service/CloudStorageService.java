package com.github.funnyzak.biz.service;

import com.github.funnyzak.biz.bean.CloudStorageObject;

import java.net.URL;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/7 6:59 PM
 * @description ICloudStorage
 */
public interface CloudStorageService {
    String defaultKey(String fileExt);

    CloudStorageObject upload(String filePath) throws Exception;

    CloudStorageObject upload(String filePath, String objectKey) throws Exception;

    void deleteByKey(String key) throws Exception;

    URL signedUrl(String bucket, String key, Integer expireSecond);

    URL signedUrl(String key, Integer expireSecond);

    URL signedUrl(String key);
}
