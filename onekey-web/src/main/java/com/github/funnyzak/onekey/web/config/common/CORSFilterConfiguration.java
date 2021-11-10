package com.github.funnyzak.onekey.web.config.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;

/**
 * @author silenceace@gmail.com
 */
@Configuration
public class CORSFilterConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        ArrayList<String> originHeaders = new ArrayList<>();
        originHeaders.add("*");
        corsConfiguration.setAllowedOrigins(originHeaders);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter bean = new CorsFilter(urlBasedCorsConfigurationSource);
        return bean;
    }
}