package com.zf1976.ant.common.component.mail;

import com.zf1976.ant.common.component.mail.impl.ValidateServiceImpl;

/**
 * @author mac
 * @date 2021/5/18
 */
public interface ValidateMobileService extends ValidateService {

    static ValidateMobileService validateMobileService() {
        return ValidateServiceImpl.getInstance();
    }

    /**
     * 发送手机验证码
     *
     * @param mobile 手机
     * @return {@link Void}
     */
    Void sendMobileVerifyCode(String mobile);

    /**
     * 校验验证码
     *
     * @param code   验证码
     * @param mobile 手机
     * @return {@link boolean}
     */
    boolean validateMobileVerifyCode(String mobile, String code);

    /**
     * 清除验证码
     *
     * @param mobile 手机
     */
    void clearMobileVerifyCode(String mobile);
}
