package com.zf1976.ant.auth;

/**
 * @author ant
 * Create by Ant on 2021/2/16 12:54 PM
 */
public interface AuthConstants {

    /**
     * oauth 缓存前缀
     */
    String PROJECT_OAUTH_ACCESS = "ant_oauth:access:";

    /**
     * oauth 缓存令牌前缀
     */
    String PROJECT_OAUTH_TOKEN = "ant_oauth:token:";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";

    /**w
     * 用户信息缓存
     */
    String USER_DETAILS = "user_details";

    /**
     * 用户id
     */
    String JWT_USER_ID_KEY = "id";

    /**
     * 客户端id
     */
    String JWT_CLIENT_ID_KEY = "client_id";

    /**
     * JWT存储权限属性
     */
    String JWT_AUTHORITIES_KEY = "authorities";

    /**
     * 签发方
     */
    String ISSUER = "issuer";

    /**
     * 过期时间
     */
    String EXPIRED = "expired";

    /**
     * jwt 唯一标示
     */
    String JTI = "jti";

    /**
     * 签发时间
     */
    String IAT = "iat";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 密码
     */
    String PASSWORD = "password";

}