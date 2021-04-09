package com.zf1976.ant.common.security.support.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mac
 * Create by Ant on 2020/9/28 23:35
 */
public class Session implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 资源所有者
     */
    private Boolean owner = Boolean.FALSE;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 操作系统类型
     */
    private String operatingSystemType;
    /**
     * IP
     */
    private String ip;
    /**
     * 地址
     */
    private String ipRegion;
    /**
     * token
     */
    private String token;
    /**
     * 登录时间
     */
    private Date loginTime;
    /**
     * 到期时间
     */
    private Date expiredTime;

    private final Map<Object, Object> attribute = new HashMap<>();

    public Long getId() {
        return id;
    }

    public Session setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Session setUsername(String username) {
        this.username = username;
        return this;
    }

    public Boolean getOwner() {
        return owner;
    }

    public Session setOwner(Boolean owner) {
        this.owner = owner;
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    public Session setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    public String getOperatingSystemType() {
        return operatingSystemType;
    }

    public Session setOperatingSystemType(String operatingSystemType) {
        this.operatingSystemType = operatingSystemType;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Session setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public Session setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Session setToken(String token) {
        this.token = token;
        return this;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public Session setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
        return this;
    }


    public void setAttribute(Object key, Object value) {
        this.attribute.put(key, value);
    }

    public void removeAttribute(Object key) {
        this.attribute.remove(key);
    }

    public Object getAttribute(Object key) {
        return this.attribute.get(key);
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public Session setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", owner=" + owner +
                ", browser='" + browser + '\'' +
                ", operatingSystemType='" + operatingSystemType + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", token='" + token + '\'' +
                ", loginTime=" + loginTime +
                ", expiredTime=" + expiredTime +
                ", attribute=" + attribute +
                '}';
    }
}
