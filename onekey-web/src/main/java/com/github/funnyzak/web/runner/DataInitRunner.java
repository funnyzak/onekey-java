package com.github.funnyzak.web.runner;

import com.github.funnyzak.biz.service.label.LabelInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/3/6 6:19 下午
 * @description InitRunner
 */
@Component
@Order(2)
public class DataInitRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LabelInfoService labelInfoService;

    @Autowired
    public DataInitRunner(LabelInfoService labelInfoService) {
        this.labelInfoService = labelInfoService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            labelInfoService.initLabelDate();
            logger.info("系统标签数据初始化完成。");
        } catch (Exception ex) {
            logger.error("不需要初始化标签数据！");
        }
    }
}