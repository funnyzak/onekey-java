package com.github.funnyzak.biz.config.tencent.sms;

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
 * TenSmsService Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>May 14, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TenSmsServiceTest {

    @Autowired
    TenSmsService tenSmsService;

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
     * Method: send(List<String> phoneNumList, SmsTemplateInfo templateInfo, List<String> templateParams)
     */
    @Test
    public void testSend() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendSms(List<String> phoneNumList, SmsFunctionType smsFunctionType, List<String> smsData)
     */
    @Test
    public void testSendSmsForPhoneNumListSmsFunctionTypeSmsData() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendSms(String phoneNum, SmsFunctionType smsFunctionType, List<String> smsData)
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
        NutMap rlt = tenSmsService.sendVerifySms("+8613810676406", "3885", 5);
        Assert.assertTrue("成功", rlt != null);
    }


} 
