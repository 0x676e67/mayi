package com.zf1976.ant.common.component.mail;

/**
 * @author mac
 */
public interface ValidateService {

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return null
     */
    Void sendMailValidate(String email);

    /**
     * 校验
     *
     * @param code 验证码
     * @param email 邮箱
     * @return ture false
     */
    Boolean validate(String email, String code);

    /**
     * 清除验证码
     *
     * @param email 邮箱
     */
    void clear(String email);
}
