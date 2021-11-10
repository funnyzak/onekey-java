package com.github.funnyzak.onekey.biz.config.tencent.captcha;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.github.funnyzak.onekey.biz.exception.BizException;
import com.github.funnyzak.onekey.common.utils.TypeParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/12 11:09 AM
 * @description TenCaptchaService
 */
@Component("TenCaptchaService")
@EnableConfigurationProperties(TenCaptchaConfigProperties.class)
@ConditionalOnProperty(value = "ten-cloud-captcha", matchIfMissing = true)
public class TenCaptchaService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TenCaptchaConfigProperties tenCaptchaConfigProperties;

    private CaptchaClient client;

    @Autowired
    public TenCaptchaService(TenCaptchaConfigProperties tenCaptchaConfigProperties) {
        this.tenCaptchaConfigProperties = tenCaptchaConfigProperties;
        init();
    }

    public void init() {
        if (tenCaptchaConfigProperties.getSecretId() == null) {
            return;
        }

        Credential cred = new Credential(tenCaptchaConfigProperties.getSecretId(), tenCaptchaConfigProperties.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(tenCaptchaConfigProperties.getEndpoint());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        client = new CaptchaClient(cred, "", clientProfile);
    }

    /**
     * 核查验证码票据结果 https://cloud.tencent.com/document/product/1110/36926
     *
     * @param tenCaptchaRequestInfo 验证票据请求信息
     * @throws Exception
     */
    public String describeCaptchaResult(TenCaptchaRequestInfo tenCaptchaRequestInfo) throws Exception {
        try {
            DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();

            req.setCaptchaType(tenCaptchaRequestInfo.getCaptchaType());
            req.setTicket(tenCaptchaRequestInfo.getTicket());
            req.setUserIp(tenCaptchaRequestInfo.getUserIp());
            req.setBusinessId(tenCaptchaRequestInfo.getBusinessId());
            req.setSceneId(tenCaptchaRequestInfo.getSceneId());
            req.setMacAddress(tenCaptchaRequestInfo.getMacAddress());
            req.setImei(tenCaptchaRequestInfo.getImei());
            req.setRandstr(tenCaptchaRequestInfo.getRandStr());
            req.setCaptchaAppId(TypeParse.parseLong(tenCaptchaConfigProperties.getSdkAppId()));
            req.setAppSecretKey(tenCaptchaConfigProperties.getSdkAppSecret());
            req.setNeedGetCaptchaTime(tenCaptchaRequestInfo.getNeedGetCaptchaTime());

            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);

            if (!resp.getCaptchaCode().equals(1L)) {
                logger.error("验证无效，错误为:{}", DescribeCaptchaResultResponse.toJsonString(resp));
                throw new BizException(String.format("错误码:%s,错误信息:%s", resp.getCaptchaCode(), resp.getCaptchaMsg()));
            }
            return DescribeCaptchaResultResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            logger.error("SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("SDK验证发生系统错误。", e);
            throw e;
        }
    }
}