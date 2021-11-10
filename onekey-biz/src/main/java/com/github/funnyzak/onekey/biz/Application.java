package com.github.funnyzak.onekey.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2019-08-12 17:16
 */
@SpringBootApplication(scanBasePackages = "com.github.funnyzak.onekey")
@EnableAsync
@EnableTransactionManagement
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }
}