package com.zf1976.ant.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/3/11
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public class AuthProperties {

    /**
     * jwt公钥地址
     */
    private String jwkSetUri;

    /**
     * jwt校验地址
     */
    private String jwtCheckUri;

}
