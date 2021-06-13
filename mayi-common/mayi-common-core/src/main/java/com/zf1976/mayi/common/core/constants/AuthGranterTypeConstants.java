package com.zf1976.mayi.common.core.constants;

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


    String[] ARRAY = new String[]{PASSWORD, PASSWORD_CODE, CLIENT_CREDENTIALS, AUTHORIZATION_CODE, IMPLICIT};

}
