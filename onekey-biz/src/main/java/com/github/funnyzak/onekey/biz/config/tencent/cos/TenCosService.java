package com.github.funnyzak.onekey.biz.config.tencent.cos;

import com.github.funnyzak.onekey.biz.service.CloudStorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.PersistableUpload;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.github.funnyzak.onekey.biz.bean.CloudStorageObject;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.common.utils.DateUtils;
import com.github.funnyzak.onekey.common.utils.FileUtils;
import com.github.funnyzak.onekey.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/12/16 5:51 下午
 * @description CosFileManager
 */
@Component("TenCosFileManager")
@EnableConfigurationProperties(TenCosConfigProperties.class)
@ConditionalOnProperty(value = "ten-cloud-cos", matchIfMissing = true)
public class TenCosService implements CloudStorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TenCosConfigProperties cosConfigProperties;

    @Autowired
    public TenCosService(TenCosConfigProperties cosConfigProperties) {
        this.cosConfigProperties = cosConfigProperties;
        init();
    }

    public COSClient cosClient;

    public TransferManager transferManager;

    public void init() {
        if (cosConfigProperties.getSecretId() == null) {
            return;
        }
        COSCredentials cred = new BasicCOSCredentials(cosConfigProperties.getSecretId(), cosConfigProperties.getSecretKey());
        Region region = new Region(cosConfigProperties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        cosClient = new COSClient(cred, clientConfig);

        // 线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(16);
        // 传入一个 threadPool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        transferManager = new TransferManager(cosClient, threadPool);
        // 设置高级接口的分块上传阈值和分块大小为10MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(10 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(10 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
    }

    /**
     * 获取默认存储Key
     *
     * @param fileExt 后缀名如 png
     * @return
     */
    public String defaultKey(String fileExt) {
        return String.format("%s/%s/%s.%s", cosConfigProperties.getPrefixKey(),
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
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            throw new BizException("本地文件不存在");
        }

        if (StringUtils.isNullOrEmpty(key)) {
            key = defaultKey(FileUtils.getFileExt(filePath));
        } else {
            key = cosConfigProperties.getPrefixKey() + (key.indexOf("/") == 0 ? key : ("/" + key));
        }
        return localFile.length() < 20971520 ? uploadMinFile(localFile, key) : advanceFile(localFile, key);
    }

    public CloudStorageObject advanceFile(File localFile, String key) throws Exception {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfigProperties.getBucket(), key, localFile);
            PersistableUpload persistableUpload = null;
            Upload upload = transferManager.upload(putObjectRequest);
            // 等待"分块上传初始化"完成，并获取到 persistableUpload （包含uploadId等）
            while (persistableUpload == null) {
                persistableUpload = upload.getResumeableMultipartUploadId();
                Thread.sleep(100);
            }
            // 步骤二：当由于网络等问题，大文件的上传被中断，则根据 PersistableUpload 恢复该文件的上传，只上传未上传的分块
            Upload newUpload = transferManager.resumeUpload(persistableUpload);
            // 等待传输结束（如果想同步的等待上传结束，则调用 waitForCompletion）
            newUpload.waitForCompletion();

            return new CloudStorageObject(cosConfigProperties.getDomain(), key, cosConfigProperties.getBucket(), cosConfigProperties.getRegion());
        } catch (Exception ex) {
            logger.error("COS上传小文件失败，错误信息：", ex);
            throw new BizException(ex.getMessage());
        }
    }

    /**
     * 上传小于20M文件
     */
    public CloudStorageObject uploadMinFile(File localFile, String key) throws Exception {
        try {
            String bucketName = cosConfigProperties.getBucket();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            String etag = putObjectResult.getETag();

            return !StringUtils.isNullOrEmpty(etag) ? new CloudStorageObject(cosConfigProperties.getDomain(), key, cosConfigProperties.getBucket(), cosConfigProperties.getRegion()) : null;
        } catch (Exception ex) {
            logger.error("COS上传小文件失败，错误信息：", ex);
            throw new BizException(ex.getMessage());
        }
    }

    @Override
    public void deleteByKey(String key) throws Exception {
        try {
            cosClient.deleteObject(cosConfigProperties.getBucket(), key);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public URL signedUrl(String bucket, String key, Integer expireSecond) {
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key, HttpMethodName.GET);
        // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
        Date expirationDate = new Date(System.currentTimeMillis() + expireSecond * 1000L);
        req.setExpiration(expirationDate);
        return cosClient.generatePresignedUrl(req);
    }

    @Override
    public URL signedUrl(String key, Integer expireSecond) {
        return signedUrl(cosConfigProperties.getBucket(), key, expireSecond);
    }

    @Override
    public URL signedUrl(String key) {
        return signedUrl(cosConfigProperties.getBucket(), key, 30 * 60);
    }
}