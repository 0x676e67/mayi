package com.zf1976.mayi.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/3/11
 **/
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

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getJwtCheckUri() {
        return jwtCheckUri;
    }

    public void setJwtCheckUri(String jwtCheckUri) {
        this.jwtCheckUri = jwtCheckUri;
    }
}
