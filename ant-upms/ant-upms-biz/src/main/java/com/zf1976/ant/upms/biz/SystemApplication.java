package com.zf1976.ant.upms.biz;

import com.zf1976.ant.upms.biz.rpc.SecurityClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Repository;

/**
 * @author mac
 * @date 2021/1/14
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@MapperScan(value = "com.zf1976", annotationClass = Repository.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = SecurityClient.class)
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

}
