package com.xz;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
@SpringBootApplication
@MapperScan("com.xz.mapper")
public class Application {
    public static void main(String[] args) {
        //mvn package spring-boot:repackage
        SpringApplication.run(Application.class,args);

    }
}
