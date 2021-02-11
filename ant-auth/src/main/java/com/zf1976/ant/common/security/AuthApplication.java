package com.zf1976.ant.common.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Repository;

/**
 * @author mac
 * @date 2021/2/10
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@MapperScan(value = "com.zf1976", annotationClass = Repository.class)
@EnableDiscoveryClient
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
