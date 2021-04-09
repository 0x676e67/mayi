package com.zf1976.ant.common.security.pojo.dto;

import lombok.Data;

/**
 * @author ant
 * Create by Ant on 2020/9/14 4:10 下午
 */
@Deprecated
public class LoginDTO {

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

    public String getPassword() {
        return password;
    }

    public LoginDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public LoginDTO setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCode() {
        return code;
    }

    public LoginDTO setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "password='" + password + '\'' +
                ", uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
