package com.github.funnyzak.biz.config.jisu.sms;

import com.github.funnyzak.biz.service.CloudSmsService;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.config.bean.sms.SmsTemplateInfo;
import com.github.funnyzak.biz.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019-08-14 08:31
 */
@Component("JsSuSmsService")
@EnableConfigurationProperties(JiSuSmsConfigProperties.class)
@ConditionalOnProperty(value = "ji-su-api.sms", matchIfMissing = true)
public class JsSuSmsService implements CloudSmsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JiSuSmsConfigProperties jiSuSmsConfigProperties;

    @Autowired
    public JsSuSmsService(JiSuSmsConfigProperties jiSuSmsConfigProperties) {
        this.jiSuSmsConfigProperties = jiSuSmsConfigProperties;
    }

    @Override
    public NutMap sendSms(List<String> phoneNumList, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        SmsTemplateInfo templateInfo = jiSuSmsConfigProperties.searchTemplate(smsFunctionType);
        if (templateInfo == null) {
            throw new Exception("没有设置短信模板");
        }

        NutMap retMap = new NutMap();
        List<LinkedHashMap> list = new ArrayList<>();
        for (String phoneNum : phoneNumList) {
            Map<String, Object> sendData = new LinkedHashMap<>();
            sendData.put("appkey", jiSuSmsConfigProperties.getAppKey());
            sendData.put("mobile", phoneNum);

            sendData.put("content", templateInfo.getTplContent().indexOf("$$") >= 0 && smsData!=null? String.format(templateInfo.getTplContent().replace("$$", "%s"), smsData.values().toArray()):templateInfo.getTplContent());
            retMap.put("msgContent", sendData.get("content"));

            Response response = Http.post2(jiSuSmsConfigProperties.getEndpoint() + "/send", sendData, 5000);
            LinkedHashMap rlt = (LinkedHashMap) Json.fromJson(response.getContent());
            if (rlt != null && "0".equals(rlt.get("status").toString())) {
                list.add(rlt);
            } else {
                logger.error("使用极速API发送短信失败，发送信息：{},{}。报错信息：{}", sendData.get("mobile"), sendData.get("content"), response.getContent());
                throw new BizException("使用极速API发送短信失败");
            }
        }
        retMap.put("originString", Json.toJson(list));
        return retMap;
    }

    @Override
    public NutMap sendSms(String phoneNum, PmUseType smsFunctionType, Map<String, String> smsData) throws Exception {
        return sendSms(Arrays.asList(phoneNum), smsFunctionType, smsData);
    }

    @Override
    public NutMap sendVerifySms(String phoneNum, String code, Integer expireMinute) throws Exception {
        Map<String, String> mapData = new LinkedHashMap<>();
        mapData.put("1", code);
        mapData.put("2", expireMinute.toString());
        return sendSms(phoneNum, PmUseType.COMMON_VERIFY, mapData);
    }
}