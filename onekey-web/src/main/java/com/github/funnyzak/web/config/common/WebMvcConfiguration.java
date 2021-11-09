package com.github.funnyzak.web.config.common;

import com.github.funnyzak.biz.config.upload.FileUploadProperties;
import com.github.funnyzak.web.hanlder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author silenceace@gmail.com
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Value("${spring.resources.static-locations}")
    private String staticLocation;

    @Value("${spring.profiles.active:dev}")
    private String profile;

    private FileUploadProperties fileUploadProperties;

    public WebMvcConfiguration() {
        super();
    }

    @Autowired
    public WebMvcConfiguration(FileUploadProperties fileUploadProperties) {
        this.fileUploadProperties = fileUploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 本地文件虚拟路径映射
        registry.addResourceHandler(fileUploadProperties.getVirtualPath() + "/**").addResourceLocations("file://" + fileUploadProperties.getLocalSavePath() + "/");

        // 重写默认静态配置
        registry.addResourceHandler("/**").addResourceLocations(staticLocation.split(","));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //设置允许跨域的路径
                .allowedOrigins("*")//设置允许跨域请求的域名
                .allowedHeaders("*")
                .allowCredentials(true)//是否允许证书 不再默认开启
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")//设置允许的方法
                .maxAge(3600);//跨域允许时间
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(CurrentMemberMethodArgumentResolver());
        argumentResolvers.add(CurrentTTokenMethodArgumentResolver());
        argumentResolvers.add(CurrentConnectorMethodArgumentResolver());
        argumentResolvers.add(currentOpenRequestMethodArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    public CurrentMemberMethodArgumentResolver CurrentMemberMethodArgumentResolver() {
        return new CurrentMemberMethodArgumentResolver();
    }

    @Bean
    public CurrentConnectorMethodArgumentResolver CurrentConnectorMethodArgumentResolver() {
        return new CurrentConnectorMethodArgumentResolver();
    }

    @Bean
    public CurrentTTokenMethodArgumentResolver CurrentTTokenMethodArgumentResolver() {
        return new CurrentTTokenMethodArgumentResolver();
    }

    @Bean
    public CurrentOpenRequestMethodArgumentResolver currentOpenRequestMethodArgumentResolver() {
        return new CurrentOpenRequestMethodArgumentResolver();
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

}