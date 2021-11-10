package com.github.funnyzak.onekey.biz.config.tencent.sms;

import com.github.funnyzak.onekey.biz.service.CloudSmsService;
import com.jayway.jsonpath.JsonPath;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.onekey.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.onekey.biz.config.bean.sms.SmsTemplateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/12 11:11 AM
 * @description TenSmsService
 */
@Component("TenSmsService")
@EnableConfigurationProperties(TenSmsConfigProperties.class)
@ConditionalOnProperty(value = "ten-cloud-sms", matchIfMissing = true)
public class TenSmsService implements CloudSmsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TenSmsConfigProperties tenSmsConfigProperties;

    private SmsClient client;

    @Autowired
    public TenSmsService(TenSmsConfigProperties tenSmsConfigProperties) {
        this.tenSmsConfigProperties = tenSmsConfigProperties;
        init();
    }

    public void init() {
        if (tenSmsConfigProperties.getSecretId() == null) {
            return;
        }

        Credential cred = new Credential(tenSmsConfigProperties.getSecretId(), tenSmsConfigProperties.getSecretKey());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(tenSmsConfigProperties.getEndpoint());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        client = new SmsClient(cred, "", clientProfile);
    }

    /**
     * 短信发送
     * 发送短信接口 https://cloud.tencent.com/document/product/382/38778
     *
     * @param phoneNumList   手机号列表
     * @param templateInfo   所使用模板
     * @param templateParams 模板参数
     * @throws Exception
     */
    public NutMap send(List<String> phoneNumList, SmsTemplateInfo templateInfo, Map<String, String> templateParams) throws Exception {
        if (templateInfo == null) {
            throw new Exception("没有设置短信模板");
        }
        try {
            NutMap retMap = new NutMap();
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = phoneNumList.stream().map(v -> v.startsWith("+") ? v : ("+86" + v)).collect(Collectors.toList()).toArray(new String[phoneNumList.size()]);
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppid(tenSmsConfigProperties.getSdkAppId());
            req.setSign(templateInfo.getSign());

            req.setTemplateID(templateInfo.getTplId());
            String[] templateParamSet1 = templateParams.values().toArray(new String[templateParams.size()]);
            req.setTemplateParamSet(templateParamSet1);
            SendSmsResponse resp = client.SendSms(req);

            retMap.put("msgContent", String.format(templateInfo.getTplContent().replace("$$", "%s"), templateParams.values().toArray(new String[templateParams.size()])));
            retMap.put("originString", SendSmsResponse.toJsonString(resp));
            return retMap;
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云发送短信时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云发送短信时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    @Override
    public NutMap sendSms(List<String> phoneNumList, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        return send(phoneNumList, tenSmsConfigProperties.searchTemplate(smsFunctionType), smsData);
    }

    @Override
    public NutMap sendSms(String phoneNum, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        NutMap retMap = send(Arrays.asList(phoneNum), tenSmsConfigProperties.searchTemplate(smsFunctionType), smsData);
        String code = JsonPath.read(retMap.get("originString").toString(), "$.SendStatusSet[0].Code");
        if (!code.equalsIgnoreCase("ok")) {
            String errMsg = JsonPath.read(retMap, "$.SendStatusSet[0].Code");
            throw new Exception(errMsg);
        }
        return retMap;
    }

    @Override
    public NutMap sendVerifySms(String phoneNum, String code, Integer expireMinute) throws Exception {
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("1", code);
        mapData.put("2", expireMinute.toString());
        return sendSms(phoneNum, PmUseType.COMMON_VERIFY, mapData);
    }
}