package com.github.funnyzak.biz.config.geoip;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import com.github.funnyzak.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Potato (silenceace@gmail.com)
 * @date 2020/4/29 3:09 下午
 * @description GeoIpManager
 */
@Configuration
@EnableConfigurationProperties(GeoIpProperties.class)
@ConditionalOnProperty(value = "geo-ip", matchIfMissing = true)
public class GeoIpManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GeoIpProperties geoIpProperties;
    private DatabaseReader databaseReader;

    public GeoIpManager(GeoIpProperties geoIpProperties) {
        this.geoIpProperties = geoIpProperties;
    }

    private DatabaseReader getDB() {
        try {
            synchronized (GeoIpManager.class) {
                if (null == this.databaseReader) {
                    File db = new File(this.geoIpProperties.getDatabase());
                    if (this.geoIpProperties.getCache()) {
                        this.databaseReader = new DatabaseReader.Builder(db).withCache(new CHMCache()).build();
                    } else {
                        this.databaseReader = new DatabaseReader.Builder(db).build();
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("读取GeoIp DB失败 ==>", ex);
        } catch (Exception ex) {
            logger.error("Init GeoIP DB Error ==>", ex);
        }
        return this.databaseReader;
    }

    public GeoLocation search(String ip) throws Exception {
        if (StringUtils.isNullOrEmpty(ip)) {
            return null;
        }

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = this.getDB().city(ipAddress);
        Country country = response.getCountry();
        Subdivision subdivision = response.getMostSpecificSubdivision();
        City city = response.getCity();
        Postal postal = response.getPostal();
        Location location = response.getLocation();

        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setCountryIsoCode(country.getIsoCode());
        geoLocation.setCountryName(country.getName());
        geoLocation.setCountryNames(country.getNames());
        geoLocation.setSubdivisionIsoCode(subdivision.getIsoCode());
        geoLocation.setSubdivisionName(subdivision.getName());
        geoLocation.setCityName(city.getName());
        geoLocation.setCityNames(city.getNames());
        geoLocation.setPostalCode(postal.getCode());
        geoLocation.setLongitude(location.getLongitude());
        geoLocation.setLatitude(location.getLatitude());

        return geoLocation;
    }
}