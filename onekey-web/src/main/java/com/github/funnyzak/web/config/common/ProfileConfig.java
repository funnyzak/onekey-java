package com.github.funnyzak.web.config.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019/10/20 9:20 PM
 * @description ProfileConfig
 */
@Configuration
public class ProfileConfig {
    public static final String LOCAL_PROFILE = "local";
    public static final String DEV_PROFILE = "dev";
    public static final String TEST_PROFILE = "test";
    public static final String PRO_PROFILE = "prod";

    @Autowired
    private ApplicationContext context;

    public String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }
}
