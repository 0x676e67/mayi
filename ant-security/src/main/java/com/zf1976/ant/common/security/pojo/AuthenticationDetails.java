package com.zf1976.ant.common.security.pojo;

import lombok.Data;

/**
 * @author ant
 * Create by Ant on 2020/9/14 4:10 下午
 */
@Data
public class AuthenticationDetails {

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
