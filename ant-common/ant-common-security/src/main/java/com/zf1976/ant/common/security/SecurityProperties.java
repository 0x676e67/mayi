package com.zf1976.ant.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全信息配置
 *
 * @author mac
 * Create by Ant on 2020/9/1 上午11:45
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 管理员marked
     */
    private String admin;

    /**
     * token header
     */
    private String tokenHeader;

    /**
     * 签发方
     */
    private String tokenIssuer;

    /**
     * 是否限制单用户登陆
     */
    private Boolean tokenSingle;

    /**
     * authorities key
     */
    private String tokenAuthoritiesKey;

    /**
     * 令牌前缀，最后留个空格 Bearer
     */
    private String prefixToken;

    /**
     * 必须使用最少88位的Base64对该令牌进行编码
     */
    private String tokenBase64Secret;

    /**
     * 令牌过期时间 此处单位/毫秒
     */
    private Long tokenExpiredTime;

    /**
     * token 续期检查
     */
    private Long tokenDetect;

    /**
     * 续期时间
     */
    private Long tokenRestore;

    /**
     * session id
     */
    private String prefixSessionId;

    /**
     * session token
     */
    private String prefixSessionToken;

    /**
     * 是否开启续期
     */
    private Boolean enableRestore;

    /**
     * jwt 证书密钥
     */
    private String rsaSecret;

    /**
     * 白名单 uri
     */
    private String[] ignoreUri;

}
