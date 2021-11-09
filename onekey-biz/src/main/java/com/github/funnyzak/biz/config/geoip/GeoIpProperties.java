package com.github.funnyzak.biz.config.geoip;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/4/29 3:04 下午
 * @description GeoIpProperties
 */
@Data
@ConfigurationProperties(ignoreInvalidFields = true, prefix = "geo-ip")
public class GeoIpProperties {
    /**
     * DB File Path
     */
    private String database;

    /**
     * 是否使用缓存，使用缓存将节省内存开销
     */
    private Boolean cache = true;
}