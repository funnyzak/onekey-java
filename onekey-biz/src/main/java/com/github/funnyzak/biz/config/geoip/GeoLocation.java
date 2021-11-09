package com.github.funnyzak.biz.config.geoip;

import lombok.Data;

import java.util.Map;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/4/29 7:10 下午
 * @description GeoLocation
 */
@Data
public class GeoLocation {
    /**
     * 国家代码
     */
    private String countryIsoCode;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * 国家名称（国际化）
     */
    private Map<String, String> countryNames;

    /**
     * 细分地域代码
     */
    private String subdivisionIsoCode;

    /**
     * 细分地域名称
     */
    private String subdivisionName;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 城市名称（多语言）
     */
    private Map<String, String> cityNames;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;
}