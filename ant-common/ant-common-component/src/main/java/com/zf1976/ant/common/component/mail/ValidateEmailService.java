package com.zf1976.ant.common.component.mail;

import com.zf1976.ant.common.component.mail.impl.ValidateServiceImpl;

/**
 * @author mac
 * @date 2021/5/18
 */
public interface ValidateEmailService extends ValidateService {

    static ValidateEmailService validateEmailService() {
        return ValidateServiceImpl.getInstance();
    }

    /**
     * 发送验证码
     *
     * @param key 键
     * @return null
     */
    Void sendEmailVerifyCode(String key);

    /**
     * 校验
     *
     * @param code 验证码
     * @param key  键
     * @return ture false
     */
    Boolean validateEmailVerifyCode(String key, String code);

    /**
     * 清除验证码
     *
     * @param key 键
     */
    void clearEmailVerifyCode(String key);
}
