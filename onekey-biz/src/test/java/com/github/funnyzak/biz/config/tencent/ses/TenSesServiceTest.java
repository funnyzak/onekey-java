package com.github.funnyzak.biz.config.tencent.ses;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TenSesService Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>May 17, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TenSesServiceTest {
    @Autowired
    TenSesService tenSesService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: init()
     */
    @Test
    public void testInit() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSendEmailStatus(String messageId, String ToEmailAddress, Long requestDateTs, Integer offset, Integer limit)
     */
    @Test
    public void testGetSendEmailStatus() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: createEmailTemplate(String templateName, String html, String text)
     */
    @Test
    public void testCreateEmailTemplate() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: ListEmailTemplates(Integer limit, Integer offset)
     */
    @Test
    public void testListEmailTemplates() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: updateEmailTemplate(String html, String text, String templateId, String templateName)
     */
    @Test
    public void testUpdateEmailTemplate() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendTextMail(String destination, String subject, String text)
     */
    @Test
    public void testSendTextMailForDestinationSubjectText() throws Exception {
//TODO: Test goes here... 
    }

    String testEmailAddress = "332488355@qq.com";

    /**
     * Method: sendTextMail(String destination, String subject, String text, String fromEmail, String replayToAddress)
     */
    @Test
    public void testSendTextMailForDestinationSubjectTextFromEmailReplayToAddress() throws Exception {
        String rlt = tenSesService.sendTextMail(testEmailAddress, "这是一封text邮件", "这是text邮件内容");
        Assert.assertTrue("成功", rlt != null);

    }

    /**
     * Method: sendHtmlMail(String destination, String subject, String html)
     */
    @Test
    public void testSendHtmlMailForDestinationSubjectHtml() throws Exception {
        String rlt = tenSesService.sendHtmlMail(testEmailAddress, "这是一封html邮件", "<p>这是HTML邮件，呵呵呵</p><br/><h1>大标题</h1>");
        Assert.assertTrue("成功", rlt != null);
    }

    /**
     * Method: sendHtmlMail(String destination, String subject, String html, String fromEmail, String replayToAddress)
     */
    @Test
    public void testSendHtmlMailForDestinationSubjectHtmlFromEmailReplayToAddress() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendTplMail(String destination, PmUseType pmUseType, String subject, String templateData)
     */
    @Test
    public void testSendTplMailForDestinationPmUseTypeSubjectTemplateData() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendTplMail(String destination, PmUseType pmUseType, String subject, String templateId, String templateData)
     */
    @Test
    public void testSendTplMailForDestinationPmUseTypeSubjectTemplateIdTemplateData() throws Exception {
//        String rlt = tenSesService.sendTplMail(testEmailAddress, PmUseType.REGISTER_SUCCESS_NOTICE, null, "{\"name\":\"王二小\"}");
//        Assert.assertTrue("成功", rlt != null);
    }

    /**
     * Method: sendTplMail(String destination, PmUseType pmUseType, String subject, String templateId, String templateData, String fromEmailAddress, String replayToAddress)
     */
    @Test
    public void testSendTplMailForDestinationPmUseTypeSubjectTemplateIdTemplateDataFromEmailAddressReplayToAddress() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendMail(List<String> destinations, PmUseType pmUseType, String subject, String templateId, String templateData, String text, String html, String fromEmailAddress, String replayToAddress)
     */
    @Test
    public void testSendMail() throws Exception {
//TODO: Test goes here... 
    }


} 
