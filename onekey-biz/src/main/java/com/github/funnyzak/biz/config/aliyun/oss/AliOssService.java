package com.github.funnyzak.biz.config.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.funnyzak.biz.service.CloudStorageService;
import com.github.funnyzak.biz.bean.CloudStorageObject;
import com.github.funnyzak.biz.exception.BizException;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.FileUtils;
import com.github.funnyzak.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/7 6:29 PM
 * @description AliOssFileManager
 */
@Component("AliOssFileManager")
@EnableConfigurationProperties(AliOssConfigProperties.class)
@ConditionalOnProperty(value = "ali-cloud-oss", matchIfMissing = true)
public class AliOssService implements CloudStorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AliOssConfigProperties aliOssConfigProperties;

    private OSS ossClient;

    @Autowired
    public AliOssService(AliOssConfigProperties aliOssConfigProperties) {
        this.aliOssConfigProperties = aliOssConfigProperties;
        init();
    }

    public void init() {
        if (aliOssConfigProperties.getAccessKeyId() == null) {
            return;
        }
        ossClient = new OSSClientBuilder().build(aliOssConfigProperties.getEndPoint(), aliOssConfigProperties.getAccessKeyId(), aliOssConfigProperties.getAccessKeySecret());
    }

    /**
     * 获取默认存储Key
     *
     * @param fileExt 后缀名如 png
     * @return
     */
    @Override
    public String defaultKey(String fileExt) {
        return String.format("%s/%s/%s.%s", aliOssConfigProperties.getPrefixKey(),
                DateUtils.format("yyyyMMdd", new Date()),
                StringUtils.getUUIDNumberOnly().toLowerCase(),
                fileExt);
    }

    @Override
    public CloudStorageObject upload(String filePath) throws Exception {
        return upload(filePath, null);
    }

    /**
     * 上传本地文件
     *
     * @param filePath
     * @return
     */
    @Override
    public CloudStorageObject upload(String filePath, String key) throws Exception {
        if (!new File(filePath).exists()) {
            throw new BizException("上传文件不存在");
        }

        if (StringUtils.isNullOrEmpty(key)) {
            key = defaultKey(FileUtils.getFileExt(filePath));
        } else {
            key = aliOssConfigProperties.getPrefixKey() + (key.indexOf("/") == 0 ? key : ("/" + key));
        }

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream(filePath);
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(aliOssConfigProperties.getBucketName(), key, inputStream);
        return new CloudStorageObject(aliOssConfigProperties.getDomain(), key, aliOssConfigProperties.getBucketName(), aliOssConfigProperties.getEndPoint());
    }

    @Override
    public void deleteByKey(String key) throws Exception {
        try {
            // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
            ossClient.deleteObject(aliOssConfigProperties.getBucketName(), key);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public URL signedUrl(String bucket, String key, Integer expireSecond) {
        // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
        Date expirationDate = new Date(System.currentTimeMillis() + expireSecond * 1000L);
        return ossClient.generatePresignedUrl(bucket, key, expirationDate);
    }

    public URL signedUrl(String key, Integer expireSecond) {
        return signedUrl(aliOssConfigProperties.getBucketName(), key, expireSecond);
    }

    public URL signedUrl(String key) {
        return signedUrl(aliOssConfigProperties.getBucketName(), key, 30 * 60);
    }
}