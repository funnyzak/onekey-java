package com.github.funnyzak.onekey.biz.config.aliyun.sms;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * AliSmsService Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>Jul 22, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AliSmsServiceTest {

    @Autowired
    AliSmsService aliSmsService;

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
     * Method: send(List<String> phoneNumList, SmsTemplateInfo templateInfo, Map<String, String> templateParams)
     */
    @Test
    public void testSend() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendSms(List<String> phoneNumList, PmUseType smsFunctionType, Map<String, String> smsData)
     */
    @Test
    public void testSendSmsForPhoneNumListSmsFunctionTypeSmsData() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendSms(String phoneNum, PmUseType smsFunctionType, Map<String, String> smsData)
     */
    @Test
    public void testSendSmsForPhoneNumSmsFunctionTypeSmsData() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendVerifySms(String phoneNum, String code, Integer expireMinute)
     */
    @Test
    public void testSendVerifySms() throws Exception {
        NutMap rltMap = aliSmsService.sendVerifySms("13611036038", "6666", 5);
        Assert.assertTrue("成功上传", rltMap != null);
    }


} 
