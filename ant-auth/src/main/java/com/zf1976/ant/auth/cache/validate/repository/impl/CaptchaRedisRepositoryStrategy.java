package com.zf1976.ant.auth.cache.validate.repository.impl;

import com.power.common.util.StringUtil;
import com.zf1976.ant.auth.cache.validate.repository.CaptchaRepositoryStrategy;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午2:11
 */
public class CaptchaRedisRepositoryStrategy implements CaptchaRepositoryStrategy {

    private static final AtomicReference<CaptchaRepositoryStrategy> STRATEGY_ATOMIC_REFERENCE = new AtomicReference<>();
    private final StringRedisTemplate stringRedisTemplate;

    private CaptchaRedisRepositoryStrategy() {
        this.stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(this.stringRedisTemplate, "redis repository cannot be null");
    }

    @Override
    public boolean save(String key, String value, Long expire, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(this.buildKey(key), value, expire, timeUnit);
            return true;
        } catch (Exception e) {
            LOG.error(e, e.getCause());
            return false;
        }
    }

    @Override
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(this.buildKey(key));
        } catch (Exception e) {
            LOG.error(e, e.getCause());
            return StringUtil.ENMPTY;
        }
    }

    @Override
    public void invalidate(String key) {
        try {
            stringRedisTemplate.delete(this.buildKey(key));
        } catch (Exception e) {
            LOG.error(e, e.getCause());
            e.printStackTrace();
        }
    }

    @Override
    public void invalidateAll() {
        throw new RuntimeException();
    }

    @Override
    public ConcurrentMap<String, String> asMap() {
        throw new RuntimeException();
    }

    @Override
    public boolean isAvailable() {
        try {
            RedisSentinelConnection connection = Objects.requireNonNull(this.stringRedisTemplate.getConnectionFactory())
                                                        .getSentinelConnection();
            return !ObjectUtils.isEmpty(connection);
        } catch (Exception e) {
            return false;
        }
    }

    public static CaptchaRepositoryStrategy getInstance() {
        return Optional.ofNullable(STRATEGY_ATOMIC_REFERENCE.get())
                       .orElseGet(() -> STRATEGY_ATOMIC_REFERENCE.updateAndGet(captchaRepositoryStrategy -> {
                           return Objects.requireNonNull(Optional.ofNullable(captchaRepositoryStrategy)
                                                                 .orElseGet(CaptchaRedisRepositoryStrategy::new));
                       }));
    }
}
