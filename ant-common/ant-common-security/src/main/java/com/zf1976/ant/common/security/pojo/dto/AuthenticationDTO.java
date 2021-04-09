package com.zf1976.ant.common.security.pojo.dto;

import lombok.Data;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:10
 */
@Deprecated
public class AuthenticationDTO {

    /**
     * 登陆用户名
     */
    private String username;

    /**
     * 登陆密码 经过加密
     */
    private String password;

    /**
     * 验证码 uuid
     */
    private String uuid;

    /**
     * 验证码
     */
    private String code;

    public String getUsername() {
        return username;
    }

    public AuthenticationDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthenticationDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public AuthenticationDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCode() {
        return code;
    }

    public AuthenticationDTO setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return "AuthenticationDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
