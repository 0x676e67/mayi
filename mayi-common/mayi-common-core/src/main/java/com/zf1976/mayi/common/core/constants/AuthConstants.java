package com.zf1976.mayi.common.core.constants;

/**
 * @author ant
 * Create by Ant on 2021/2/16 12:54 PM
 */
public interface AuthConstants {

    /**
     * oauth 缓存前缀
     */
    String PROJECT_OAUTH_ACCESS = "ant_oauth2:";

    /**
     * oauth 缓存令牌前缀
     */
    String PROJECT_OAUTH = "ant_oauth2:";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";

    /**w
     * 用户信息缓存
     */
    String DETAILS = "details";

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

    String RESOURCE_IDS = "resourceIds";

    /**
     * 空值权限
     */
    String EMPTY_AUTHORITIES_VALUE = "";

    /**
     * grant type
     */
    String GRANT_TYPE = "grant_type";

    /**
     * 资源所有者
     */
    String OWNER = "ROLE_admin";

    /**
     * 签发方
     */
    String ISSUER = "issuer";

    /**
     * 过期时间
     */
    String SESSION_EXPIRED = "session_expired";

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
