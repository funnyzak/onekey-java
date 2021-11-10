package com.github.funnyzak.onekey.biz.config.geoip;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * GeoIpManager Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>4月 30, 2020</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoIpManagerTest {

    @Autowired
    private GeoIpManager geoIpManager;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: search(String ip)
     */
    @Test
    public void testSearch() throws Exception {
        GeoLocation geoLocation = geoIpManager.search("121.69.94.90");
        System.out.print(geoLocation);
        Assert.assertTrue("true", geoLocation != null);
    }


} 
