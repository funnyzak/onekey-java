package com.github.funnyzak.web.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/1/4 3:37 下午
 * @description ElasticsearchRunner
 */
@Component
@Order(1)
public class DoRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        // do something
    }
}