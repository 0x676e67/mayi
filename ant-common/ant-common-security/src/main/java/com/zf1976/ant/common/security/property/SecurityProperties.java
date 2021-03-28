package com.zf1976.ant.common.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 安全信息配置
 *
 * @author mac
 * Create by Ant on 2020/9/1 上午11:45
 */
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 资源所有者（管理员）marked
     */
    private String owner;
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
     * logout url
     */
    private String logoutUrl;
    /**
     * 白名单 uri
     */
    private String[] ignoreUri;

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public SecurityProperties setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public SecurityProperties setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public SecurityProperties setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
        return this;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public SecurityProperties setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
        return this;
    }

    public Boolean getTokenSingle() {
        return tokenSingle;
    }

    public SecurityProperties setTokenSingle(Boolean tokenSingle) {
        this.tokenSingle = tokenSingle;
        return this;
    }

    public String getTokenAuthoritiesKey() {
        return tokenAuthoritiesKey;
    }

    public SecurityProperties setTokenAuthoritiesKey(String tokenAuthoritiesKey) {
        this.tokenAuthoritiesKey = tokenAuthoritiesKey;
        return this;
    }

    public String getPrefixToken() {
        return prefixToken;
    }

    public SecurityProperties setPrefixToken(String prefixToken) {
        this.prefixToken = prefixToken;
        return this;
    }

    public String getTokenBase64Secret() {
        return tokenBase64Secret;
    }

    public SecurityProperties setTokenBase64Secret(String tokenBase64Secret) {
        this.tokenBase64Secret = tokenBase64Secret;
        return this;
    }

    public Long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public SecurityProperties setTokenExpiredTime(Long tokenExpiredTime) {
        this.tokenExpiredTime = tokenExpiredTime;
        return this;
    }

    public Long getTokenDetect() {
        return tokenDetect;
    }

    public SecurityProperties setTokenDetect(Long tokenDetect) {
        this.tokenDetect = tokenDetect;
        return this;
    }

    public Long getTokenRestore() {
        return tokenRestore;
    }

    public SecurityProperties setTokenRestore(Long tokenRestore) {
        this.tokenRestore = tokenRestore;
        return this;
    }

    public String getPrefixSessionId() {
        return prefixSessionId;
    }

    public SecurityProperties setPrefixSessionId(String prefixSessionId) {
        this.prefixSessionId = prefixSessionId;
        return this;
    }

    public String getPrefixSessionToken() {
        return prefixSessionToken;
    }

    public SecurityProperties setPrefixSessionToken(String prefixSessionToken) {
        this.prefixSessionToken = prefixSessionToken;
        return this;
    }

    public Boolean getEnableRestore() {
        return enableRestore;
    }

    public SecurityProperties setEnableRestore(Boolean enableRestore) {
        this.enableRestore = enableRestore;
        return this;
    }

    public String getRsaSecret() {
        return rsaSecret;
    }

    public SecurityProperties setRsaSecret(String rsaSecret) {
        this.rsaSecret = rsaSecret;
        return this;
    }

    public String[] getIgnoreUri() {
        return ignoreUri;
    }

    public SecurityProperties setIgnoreUri(String[] ignoreUri) {
        this.ignoreUri = ignoreUri;
        return this;
    }

    @Override
    public String toString() {
        return "SecurityProperties{" +
                "owner='" + owner + '\'' +
                ", tokenHeader='" + tokenHeader + '\'' +
                ", tokenIssuer='" + tokenIssuer + '\'' +
                ", tokenSingle=" + tokenSingle +
                ", tokenAuthoritiesKey='" + tokenAuthoritiesKey + '\'' +
                ", prefixToken='" + prefixToken + '\'' +
                ", tokenBase64Secret='" + tokenBase64Secret + '\'' +
                ", tokenExpiredTime=" + tokenExpiredTime +
                ", tokenDetect=" + tokenDetect +
                ", tokenRestore=" + tokenRestore +
                ", prefixSessionId='" + prefixSessionId + '\'' +
                ", prefixSessionToken='" + prefixSessionToken + '\'' +
                ", enableRestore=" + enableRestore +
                ", rsaSecret='" + rsaSecret + '\'' +
                ", logoutUrl='" + logoutUrl + '\'' +
                ", ignoreUri=" + Arrays.toString(ignoreUri) +
                '}';
    }
}
