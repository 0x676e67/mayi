package com.zf1976.ant.common.component.validate.repository;


import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author WINDOWS
 */
public interface CaptchaRepository {

    /**
     * 保存验证码
     *
     * @param key      key
     * @param value    value
     * @param expire   过期时间戳
     * @param timeUnit 时间单位
     * @return boolean
     */
    void store(String key, String value, Long expire, TimeUnit timeUnit);

    /**
     * 读取存储验证码
     *
     * @param key key
     * @return 验证码
     */
    String read(String key);

    /**
     * 删除存储验证码
     *
     * @param key key
     */
    void remove(String key);

    /**
     * 是否可用
     *
     * @return true false
     */
    boolean isAvailable();

}

