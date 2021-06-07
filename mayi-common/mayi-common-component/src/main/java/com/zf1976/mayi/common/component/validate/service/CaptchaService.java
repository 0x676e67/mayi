package com.zf1976.mayi.common.component.validate.service;

import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/6/18 5:37 下午
 */
public interface CaptchaService {

    /**
     * 发送验证码
     *
     * @param key      唯一标识
     * @param value    需要发送的验证码
     * @param expire   有效时间
     * @param timeUnit 时间单位
     * @return 是否发送成功
     */
    boolean storeCaptcha(String key, String value);

    /**
     * 校验验证码
     *
     * @param prefixAndKey 前缀加key
     * @param code         需要校验的验证码
     * @return 是否正确
     */
    boolean validateCaptcha(String prefixAndKey, String code);

    /**
     * 清理验证码
     *
     * @param prefixAndKey 前缀加key
     */
    void clearCaptcha(String prefixAndKey);

}
