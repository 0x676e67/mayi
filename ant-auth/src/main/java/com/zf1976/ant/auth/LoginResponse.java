package com.zf1976.ant.auth;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:06 下午
 */
@Data
@Accessors(chain = true)
public class LoginResponse {

    /**
     * 登陆成功返回前端 信息
     */
    private String token;

    /**
     * 用户信息
     */
    private UserDetails user;

}
