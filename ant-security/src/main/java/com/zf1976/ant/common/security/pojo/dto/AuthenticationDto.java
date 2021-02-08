package com.zf1976.ant.common.security.pojo.dto;

import lombok.Data;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:10
 */
@Data
public class AuthenticationDto {

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

}
