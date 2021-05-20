package com.zf1976.ant.common.security.constant;

/**
 * @author mac
 * @date 2021/2/19
 **/
public interface AuthGranterTypeConstants {

    /**
     * 授权码模式
     */
    String AUTHORIZATION_CODE = "authorization_code";

    /**
     * 密码模式
     */
    String PASSWORD = "password";

    /**
     * 密码模式+验证码
     */
    String PASSWORD_CODE = "password_code";

    /**
     * 隐藏式
     */
    String IMPLICIT = "implicit";

    /**
     * 客户端凭证
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 账号邮箱验证码模式
     */
    String ACCESS_EMAIL_CODE = "access_email_code";

    /**
     * 账号手机验证码模式
     */
    String ACCESS_PHONE_CODE = "access_phone_code";

    String[] ARRAY = new String[]{PASSWORD, PASSWORD_CODE, CLIENT_CREDENTIALS, ACCESS_EMAIL_CODE, ACCESS_PHONE_CODE, AUTHORIZATION_CODE, IMPLICIT};

}
