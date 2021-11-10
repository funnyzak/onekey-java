package com.github.funnyzak.onekey.biz.config.jisu.sms;

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
 * JsSuSMSServiceImpl Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>May 24, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JsSuSMSServiceImplTest {

    @Autowired
    JsSuSmsService jsSuSMSService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: sendSms(List<String> phoneNumList, PmUseType smsFunctionType, List<String> smsData)
     */
    @Test
    public void testSendSmsForPhoneNumListSmsFunctionTypeSmsData() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: sendSms(String phoneNum, PmUseType smsFunctionType, List<String> smsData)
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
        NutMap rlt = jsSuSMSService.sendVerifySms("13810676406", "3885", 5);
        Assert.assertTrue("成功", rlt != null);
    }


} 
