package com.zf1976.ant.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


/**
 * @author mac
 * @date 2021/2/11
 **/
@Configuration
@EnableWebFluxSecurity
public class GatewayResourceServerConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.oauth2ResourceServer()
                    .jwt();
        httpSecurity.csrf()
                    // 关闭csrf
                    .disable()
                    // 开启跨域
                    .cors()
                    .and()
                    .authorizeExchange()
                    .pathMatchers("/actuator/**","/oauth").permitAll()
                    .anyExchange().authenticated();
        return httpSecurity.build();
    }
}
