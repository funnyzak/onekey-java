package com.github.funnyzak.biz.config.tencent.ses;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.*;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import com.github.funnyzak.biz.config.bean.enums.PmUseType;
import com.github.funnyzak.biz.config.bean.ses.SesTemplateInfo;
import com.github.funnyzak.common.utils.DateUtils;
import com.github.funnyzak.common.utils.StringUtils;
import com.github.funnyzak.common.utils.TypeParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2021/5/12 11:10 AM
 * @description TenSesService
 */
@Component("TenSesService")
@EnableConfigurationProperties(TenSesConfigProperties.class)
@ConditionalOnProperty(value = "ten-cloud-ses", matchIfMissing = true)
public class TenSesService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TenSesConfigProperties tenSesConfigProperties;

    private SesClient client;

    @Autowired
    public TenSesService(TenSesConfigProperties tenSesConfigProperties) {
        this.tenSesConfigProperties = tenSesConfigProperties;
        init();
    }

    public void init() {
        if (tenSesConfigProperties.getSecretId() == null) {
            return;
        }

        Credential cred = new Credential(tenSesConfigProperties.getSecretId(), tenSesConfigProperties.getSecretKey());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(tenSesConfigProperties.getEndpoint());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        client = new SesClient(cred, tenSesConfigProperties.getRegion(), clientProfile);
    }

    /**
     * 把对象转化为小写key的map
     *
     * @param info        对象尸体
     * @param preKey      前置key eg: m_
     * @param leaveFields 保留字段列表，每个用半角逗号分割。为空则保留所有
     * @param <T>
     * @return
     */
    public <T> Map<String, String> parseEntityToLowCaseKeyMap(T info, String preKey, String leaveFields) {
        Map<String, Object> map = Json.fromJson(Map.class, Json.toJson(info));
        Map<String, String> newMap = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            if ((!StringUtils.isNullOrEmpty(leaveFields) && Arrays.stream(leaveFields.split(",")).distinct().collect(Collectors.toList()).contains(key)) || StringUtils.isNullOrEmpty(leaveFields)) {
                newMap.put((preKey + key).toLowerCase(), TypeParse.parseString(map.get(key)));
            }
        }
        return newMap;
    }

    /**
     * 设置模板内容
     *
     * @param templateContent
     * @param templateData
     * @return
     */
    public String setTemplateContent(String templateContent, Map<String, String> templateData) {
        if (templateData == null || templateData.size() == 0) {
            return templateContent;
        }
        for (String key : templateData.keySet()) {
            templateContent = templateContent.replace("{{" + key + "}}", templateData.get(key));
        }
        return templateContent;
    }

    public LinkedHashMap getSendEmailStatus(String messageId, String ToEmailAddress, Long requestDateTs, Integer offset, Integer limit) throws Exception {
        try {
            GetSendEmailStatusRequest req = new GetSendEmailStatusRequest();
            req.setMessageId(messageId);
            req.setToEmailAddress(ToEmailAddress);
            req.setRequestDate(DateUtils.sD(new Date(requestDateTs * 1000)));
            req.setOffset(TypeParse.parseLong(offset));
            req.setLimit(TypeParse.parseLong(limit));

            GetSendEmailStatusResponse resp = client.GetSendEmailStatus(req);
            return (LinkedHashMap) Json.fromJson(GetSendEmailStatusResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云获取邮件发送状态时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云获取邮件发送状态时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    public String createEmailTemplate(String templateName, String html, String text) throws Exception {
        try {
            CreateEmailTemplateRequest req = new CreateEmailTemplateRequest();
            req.setTemplateName(templateName);
            TemplateContent templateContent1 = new TemplateContent();
            templateContent1.setHtml(StringUtils.isNullOrEmpty(html) ? null : Base64.getEncoder().encodeToString(html.getBytes()));
            templateContent1.setText(StringUtils.isNullOrEmpty(text) ? null : Base64.getEncoder().encodeToString(text.getBytes()));
            req.setTemplateContent(templateContent1);

            CreateEmailTemplateResponse resp = client.CreateEmailTemplate(req);

            return resp.getRequestId();
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云创建邮件模板时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云创建邮件模板时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    public LinkedHashMap listEmailTemplates(Integer limit, Integer offset) throws Exception {
        try {
            ListEmailTemplatesRequest req = new ListEmailTemplatesRequest();
            req.setLimit(TypeParse.parseLong(limit));
            req.setOffset(TypeParse.parseLong(offset));
            ListEmailTemplatesResponse resp = client.ListEmailTemplates(req);
            return (LinkedHashMap) Json.fromJson(ListEmailTemplatesResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云获取邮件模板时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云获取邮件模板时，SDK验证发生系统错误。", e);
            throw e;
        }
    }


    /**
     * 更新邮件模板
     *
     * @param html         简单HTML信息，任选其一
     * @param text         简单文本信息，任选其一
     * @param templateId   模板ID
     * @param templateName 模板名称
     * @return 返回RequestId
     * @throws Exception
     */
    public String updateEmailTemplate(String html, String text, String templateId, String templateName) throws Exception {
        try {
            UpdateEmailTemplateRequest req = new UpdateEmailTemplateRequest();
            TemplateContent templateContent1 = new TemplateContent();
            templateContent1.setHtml(StringUtils.isNullOrEmpty(html) ? null : Base64.getEncoder().encodeToString(html.getBytes()));
            templateContent1.setText(StringUtils.isNullOrEmpty(text) ? null : Base64.getEncoder().encodeToString(text.getBytes()));
            req.setTemplateContent(templateContent1);

            req.setTemplateID(TypeParse.parseLong(templateId));
            req.setTemplateName(templateName);

            UpdateEmailTemplateResponse resp = client.UpdateEmailTemplate(req);

            return resp.getRequestId();
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云更新邮件模板时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云更新邮件模板时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    /**
     * 获取模板内容
     *
     * @param templateId
     * @return
     * @throws Exception
     */
    public LinkedHashMap getEmailTemplate(String templateId) throws Exception {
        try {
            GetEmailTemplateRequest req = new GetEmailTemplateRequest();
            req.setTemplateID(TypeParse.parseLong(templateId));

            GetEmailTemplateResponse resp = client.GetEmailTemplate(req);
            return (LinkedHashMap) Json.fromJson(GetEmailTemplateResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云获取邮件模板时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云获取邮件模板时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    /**
     * 删除邮件模板
     *
     * @param templateId 模板ID
     * @return
     * @throws Exception
     */
    public String delEmailTemplate(String templateId) throws Exception {
        try {
            DeleteEmailTemplateRequest req = new DeleteEmailTemplateRequest();
            req.setTemplateID(TypeParse.parseLong(templateId));

            DeleteEmailTemplateResponse resp = client.DeleteEmailTemplate(req);
            return resp.getRequestId();
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云删除邮件模板时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云删除邮件模板时，SDK验证发生系统错误。", e);
            throw e;
        }
    }

    public String sendTextMail(String destination, String subject, String text) throws Exception {
        return sendTextMail(destination, subject, text, null, null);
    }

    public String sendTextMail(String destination, String subject, String text, String fromEmail, String replayToAddress) throws Exception {
        return sendMail(Arrays.asList(destination), null, subject, null, null, text, null, fromEmail, replayToAddress);
    }

    public String sendHtmlMail(String destination, String subject, String html) throws Exception {
        return sendHtmlMail(destination, subject, html, null, null);
    }

    public String sendHtmlMail(String destination, String subject, String html, String fromEmail, String replayToAddress) throws Exception {
        return sendMail(Arrays.asList(destination), null, subject, null, null, null, html, fromEmail, replayToAddress);
    }

    public String sendTplMail(String destination, PmUseType pmUseType, String subject, Map<String, String> templateData) throws Exception {
        return sendTplMail(destination, pmUseType, subject, null, templateData);
    }

    public String sendTplMail(String destination, PmUseType pmUseType, String subject, String templateId, Map<String, String> templateData) throws Exception {
        return sendTplMail(destination, pmUseType, subject, templateId, templateData, null, null);
    }

    public String sendTplMail(String destination, PmUseType pmUseType, String subject, String templateId, Map<String, String> templateData, String fromEmailAddress, String replayToAddress) throws Exception {
        return sendMail(Arrays.asList(destination), pmUseType, subject, templateId, templateData, null, null, fromEmailAddress, replayToAddress);
    }

    /**
     * 发送邮件
     *
     * @param destinations     收信人邮箱地址，最多支持群发50人。注意：邮件内容会显示所有收件人地址，非群发邮件请多次调用API发送。
     * @param pmUseType        发送用途
     * @param subject          优先级高于默认配置。邮件标题。 默认读取默认配置
     * @param templateId       选填，手动模板ID。 默认读取默认配置
     * @param templateData     选填，模板数据（当通过模板发送时）。当使用模板发送时，需设置邮件内容变量数据。模板通过用途类型读取相关模板配置。
     * @param text             选填，TXT方式发送内容，base64之后的纯文本信息，如果没有Html，邮件中会直接显示纯文本；如果有Html，它代表邮件的纯文本样式
     * @param html             选填，HTML发送发送内容，base64之后的Html代码。需要包含所有的代码信息，不要包含外部css，否则会导致显示格式错乱。
     * @param fromEmailAddress 选填，优先级高于模板发送配置。发信邮件地址。请填写发件人邮箱地址，例如：noreply@mail.qcloud.com。如需填写发件人说明，请按照 发信人 <邮件地址> 的方式填写，例如： 腾讯云团队 <noreply@mail.qcloud.com>
     * @param replayToAddress  选填，优先级高于模板发送配置。邮件的“回复”电子邮件地址。可以填写您能收到邮件的邮箱地址，可以是个人邮箱。如果不填，收件人将会回复到腾讯云。
     * @return
     * @throws Exception
     */
    public String sendMail(List<String> destinations, PmUseType pmUseType, String subject, String templateId, Map<String, String> templateData, String text, String html, String fromEmailAddress, String replayToAddress) throws Exception {
        try {
            if (destinations == null || destinations.size() == 0) {
                throw new Exception("请设置邮件接受人再发送");
            }

            SesTemplateInfo sesTemplateInfo = tenSesConfigProperties.searchTemplate(pmUseType);
            String _fromEmailAddress = !StringUtils.isNullOrEmpty(fromEmailAddress) ? fromEmailAddress : (sesTemplateInfo != null && !StringUtils.isNullOrEmpty(sesTemplateInfo.getFromEmailAddress())) ? sesTemplateInfo.getFromEmailAddress() : tenSesConfigProperties.getFromEmailAddress();
            String _replayToAddress = !StringUtils.isNullOrEmpty(replayToAddress) ? fromEmailAddress : (sesTemplateInfo != null && !StringUtils.isNullOrEmpty(sesTemplateInfo.getReplayToAddress())) ? sesTemplateInfo.getReplayToAddress() : tenSesConfigProperties.getReplayToAddress();
            Long _templateId = TypeParse.parseLong(!StringUtils.isNullOrEmpty(templateId) ? templateId : sesTemplateInfo != null ? sesTemplateInfo.getTplId() : 0L);
            String _templateData = templateData != null ? Json.toJson(templateData, JsonFormat.compact()) : null;
            String _simpleHtml = !StringUtils.isNullOrEmpty(html) ? Base64.getEncoder().encodeToString(html.getBytes()) : null;
            String _simpleTxt = !StringUtils.isNullOrEmpty(text) ? Base64.getEncoder().encodeToString(text.getBytes()) : null;
            String _subject = !StringUtils.isNullOrEmpty(subject) ? subject : (sesTemplateInfo != null && !StringUtils.isNullOrEmpty(sesTemplateInfo.getSubject())) ? sesTemplateInfo.getSubject() : "您有一封新邮件，请查收";

            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress(_fromEmailAddress);
            req.setReplyToAddresses(_replayToAddress);

            req.setDestination((String[]) destinations.toArray());

            Template template1 = new Template();
            template1.setTemplateID(_templateId);
            template1.setTemplateData(_templateData);
            req.setTemplate(template1);

            Simple simple1 = new Simple();
            simple1.setHtml(_simpleHtml);
            simple1.setText(_simpleTxt);
            req.setSimple(simple1);

            req.setSubject(_subject);

            SendEmailResponse resp = client.SendEmail(req);

            return SendEmailResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            logger.error("腾讯云发送邮件时，SDK验证发生错误。", e);
            throw e;
        } catch (Exception e) {
            logger.error("腾讯云发送邮件时，SDK验证发生系统错误。", e);
            throw e;
        }
    }
}