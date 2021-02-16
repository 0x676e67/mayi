package com.zf1976.ant.auth.cache.validate.repository.impl;

import com.zf1976.ant.auth.cache.validate.repository.CaptchaCacheRepository;
import com.zf1976.ant.auth.cache.validate.repository.CaptchaRepositoryStrategy;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author WINDOWS
 */
public class CaptchaLocalRepositoryStrategy implements CaptchaRepositoryStrategy {

    private static final AtomicReference<CaptchaRepositoryStrategy> STRATEGY_ATOMIC_REFERENCE = new AtomicReference<>();
    private final CaptchaCacheRepository captchaCacheRepository;

    private CaptchaLocalRepositoryStrategy() {
        this.captchaCacheRepository = new CaptchaCacheRepository();
    }

    @Override
    public boolean save(String key, String value, Long expire, TimeUnit timeUnit) {
        this.captchaCacheRepository.put(this.buildKey(key), value);
        return true;
    }

    public boolean save(String key, String value) {
        return this.save(key, value, null, null);
    }

    @Override
    public String get(String key) {
        return this.captchaCacheRepository.get(this.buildKey(key));
    }

    @Override
    public void invalidate(String key) {
        this.captchaCacheRepository.remove(this.buildKey(key));
    }

    @Override
    public void invalidateAll() {
        this.captchaCacheRepository.getObject()
                                   .invalidateAll();
    }

    @Override
    public ConcurrentMap<String, String> asMap() {
        return this.captchaCacheRepository.getObject()
                                          .asMap();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public static CaptchaRepositoryStrategy getInstance() {
        return Optional.ofNullable(STRATEGY_ATOMIC_REFERENCE.get())
                       .orElseGet(() -> STRATEGY_ATOMIC_REFERENCE.updateAndGet(captchaRepositoryStrategy -> {
                           return Objects.requireNonNull(Optional.ofNullable(captchaRepositoryStrategy)
                                                                 .orElseGet(CaptchaLocalRepositoryStrategy::new));
                       }));
    }
}
