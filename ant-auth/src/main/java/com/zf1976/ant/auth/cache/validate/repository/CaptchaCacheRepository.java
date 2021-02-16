package com.zf1976.ant.auth.cache.validate.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.dev.CacheProperties;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2020/11/26
 **/
public class CaptchaCacheRepository {

    private static final Log LOG = LogFactory.getLog(CaptchaCacheRepository.class);

    private static final CaptchaProperties CAPTCHA_PROPERTIES;

    private static final CacheProperties CACHE_PROPERTIES;

    protected Cache<String, String> kvCache;

    static {
        CAPTCHA_PROPERTIES = ApplicationConfigUtils.getCaptchaProperties();
        CACHE_PROPERTIES = ApplicationConfigUtils.getCacheProperties();
    }

    public CaptchaCacheRepository() {
        this.initialCache();
        this.checkStatus();
    }

    private void checkStatus() {
        Assert.notNull(this.kvCache, "Uninitialized!");
    }

    protected void initialCache() {
        this.kvCache = CacheBuilder.newBuilder()
                                   .recordStats()
                                   .concurrencyLevel(CACHE_PROPERTIES.getConcurrencyLevel())
                                   .initialCapacity(CACHE_PROPERTIES.getInitialCapacity())
                                   .maximumSize(CACHE_PROPERTIES.getMaximumSize())
                                   .expireAfterWrite(CAPTCHA_PROPERTIES.getExpiration(), TimeUnit.MILLISECONDS)
                                   .removalListener(removalNotification -> {
                                       LOG.info(removalNotification.getKey() + " " + removalNotification.getValue() + " is remove!");
                                   })
                                   .build();
    }

    public void put(String key, String value) {
        this.kvCache.put(key, value);
    }

    public String get(String key) {
        return Optional.ofNullable(this.kvCache.getIfPresent(key))
                       .orElse(StringUtil.ENMPTY);
    }

    public boolean contains(String key) {
        return !StringUtil.isEmpty(this.get(key));
    }

    public void remove(String key) {
        this.kvCache.invalidate(key);
    }

    public Cache<String, String> getObject() {
        return this.kvCache;
    }
}
