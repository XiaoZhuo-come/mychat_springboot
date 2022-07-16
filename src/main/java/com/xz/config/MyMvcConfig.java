package com.xz.config;

import com.xz.Application;
import com.xz.controller.FileHandler;
import com.xz.interceptor.TokenInterceptor;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * SpringBoot跨域
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS")
                .maxAge(3600)
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor())
                .excludePathPatterns("/user/login","/user/addUser","/error/**","/img/**","/user/addUser");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //获取jar包所在目录
        ApplicationHome h = new ApplicationHome(FileHandler.class);
        File jarF = h.getSource();
        //在jar包所在目录下生成一个upload文件夹用来存储上传的图片
        String dirPath = jarF.getParentFile().toString()+"/img/";

        registry.addResourceHandler("/img/**").addResourceLocations("file:" + dirPath);
    }
}
