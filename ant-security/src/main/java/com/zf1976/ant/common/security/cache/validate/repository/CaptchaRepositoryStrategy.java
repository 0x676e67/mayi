package com.zf1976.ant.common.security.cache.validate.repository;

import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.security.cache.validate.repository.impl.CaptchaRedisRepositoryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author WINDOWS
 */
public interface CaptchaRepositoryStrategy {

    /**
     * 日志
     */
    Log LOG = LogFactory.getLog(CaptchaRedisRepositoryStrategy.class);

    /**
     * 配置
     */
    CaptchaProperties CONFIG = SpringContextHolder.getBean(CaptchaProperties.class);

    /**
     * 保存验证码
     *
     * @param key      key
     * @param value    value
     * @param expire   过期时间戳
     * @param timeUnit 时间单位
     * @return boolean
     */
    boolean save(String key, String value, Long expire, TimeUnit timeUnit);

    /**
     * 获取存储验证码
     *
     * @param key key
     * @return 验证码
     */
    String get(String key);

    /**
     * 删除存储验证码
     *
     * @param key key
     */
    void invalidate(String key);

    /**
     *  使所有缓存失效
     */
    void invalidateAll();

    /**
     * 获取缓存视图
     *
     * @return map
     */
    ConcurrentMap<String, String> asMap();

    /**
     * 是否可用
     *
     * @return true false
     */
    boolean isAvailable();

    /**
     * cache key prefix
     *
     * @param key raw key
     * @return cache key
     */
    default String buildKey(String key) {
        return CONFIG.getKeyPrefix() + key;
    }

}

