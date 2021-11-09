package com.github.funnyzak.biz.config.wx.mp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {

    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置 微信公众号的appid
         */
        private String appId;

        /**
         * 设置 微信公众号的Secret
         */
        private String secret;

        /**
         * 设置 微信公众号消息服务器配置的token
         */
        private String token;

        /**
         * 设置 微信公众号消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }

}
