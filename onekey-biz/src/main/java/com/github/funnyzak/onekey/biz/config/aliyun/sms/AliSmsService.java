package com.github.funnyzak.onekey.biz.config.aliyun.sms;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.github.funnyzak.onekey.biz.service.CloudSmsService;
import com.jayway.jsonpath.JsonPath;
import org.nutz.json.Json;
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
 * @date 2021/7/22 11:40 AM
 * @description TenSmsService
 */
@Component("AliSmsService")
@EnableConfigurationProperties(AliSmsConfigProperties.class)
@ConditionalOnProperty(value = "ali-cloud-sms", matchIfMissing = true)
public class AliSmsService implements CloudSmsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AliSmsConfigProperties aliSmsConfigProperties;

    private com.aliyun.dysmsapi20170525.Client client;

    @Autowired
    public AliSmsService(AliSmsConfigProperties tenSmsConfigProperties) {
        this.aliSmsConfigProperties = tenSmsConfigProperties;
        init();
    }

    public void init() {
        if (aliSmsConfigProperties.getAccessKeyId() == null) {
            return;
        }

        try {
            Config config = new Config()
                    .setAccessKeyId(aliSmsConfigProperties.getAccessKeyId())
                    .setAccessKeySecret(aliSmsConfigProperties.getAccessKeySecret());
            config.endpoint = aliSmsConfigProperties.getEndpoint();
            client = new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception ex) {
            logger.error("创建阿里云短信Client失败==>", ex);
        }
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
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumList.stream().collect(Collectors.joining(",")))
                    .setSignName(templateInfo.getSign())
                    .setTemplateCode(templateInfo.getTplId())
                    .setTemplateParam(templateParams != null ? Json.toJson(templateParams) : null)
                    .setSmsUpExtendCode(null)
                    .setOutId(null);

            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            retMap.put("msgContent", templateInfo.getTplContent().indexOf("$$") >= 0 && templateParams != null ? String.format(templateInfo.getTplContent().replace("$$", "%s"), templateParams.values().toArray()) : templateInfo.getTplContent());
            retMap.put("originString", Json.toJson(sendSmsResponse.toMap()));
            return retMap;
        } catch (Exception e) {
            logger.error("阿里云发送短信时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    @Override
    public NutMap sendSms(List<String> phoneNumList, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        return send(phoneNumList, aliSmsConfigProperties.searchTemplate(smsFunctionType), smsData);
    }

    @Override
    public NutMap sendSms(String phoneNum, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        NutMap retMap = send(Arrays.asList(phoneNum), aliSmsConfigProperties.searchTemplate(smsFunctionType), smsData);
        String code = JsonPath.read(retMap.get("originString").toString(), "$.body.Code");
        if (!code.equalsIgnoreCase("ok")) {
            String errMsg = JsonPath.read(retMap, "$.body.Message");
            throw new Exception(errMsg);
        }
        return retMap;
    }

    @Override
    public NutMap sendVerifySms(String phoneNum, String code, Integer expireMinute) throws Exception {
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("code", code);
        mapData.put("minute", expireMinute.toString());
        return sendSms(phoneNum, PmUseType.COMMON_VERIFY, mapData);
    }
}