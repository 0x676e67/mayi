package com.zf1976.mayi.upms.biz.pojo.vo;


import java.util.Date;

/**
 * @author mac
 * @date 2021/1/23
 **/
public class SessionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * IP
     */
    private String ip;

    /**
     * 地址
     */
    private String ipRegion;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 到期时间
     */
    private Date expiredTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public void setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        return "SessionVO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", browser='" + browser + '\'' +
                ", loginTime=" + loginTime +
                ", expiredTime=" + expiredTime +
                '}';
    }
}
