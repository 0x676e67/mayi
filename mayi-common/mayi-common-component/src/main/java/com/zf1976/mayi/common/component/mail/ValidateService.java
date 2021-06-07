package com.zf1976.mayi.common.component.mail;

import com.zf1976.mayi.common.component.mail.impl.ValidateServiceImpl;

/**
 * 验证接口
 *
 * @author mac
 */
public interface ValidateService {

    static ValidateService validateService() {
        return ValidateServiceImpl.getInstance();
    }

    /**
     * 发送验证码
     *
     * @param key 键
     * @return null
     */
    Void sendVerifyCode(String key);

    /**
     * 校验
     *
     * @param code 验证码
     * @param key 键
     * @return ture false
     */
    Boolean validateVerifyCode(String key, String code);

    /**
     * 清除验证码
     *
     * @param key 键
     */
    void clearVerifyCode(String key);
}
