package com.zf1976.ant.gateway;

import com.zf1976.ant.gateway.feign.ClientTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author mac
 */
@SpringBootApplication(scanBasePackages = "com.zf1976")
@EnableDiscoveryClient
@EnableFeignClients
@RestController
public class GatewayApplication {

    @Autowired
    private ClientTest clientTest;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @GetMapping("/oauth/demo")
    public Mono<Object> demo(){
        return Mono.just(clientTest.test());
    }
}
