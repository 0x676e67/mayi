package com.zf1976.ant.common.component.load;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zf1976.ant.common.core.property.CacheProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author WINDOWS
 */
public abstract class AbstractCaffeineCache<K, V> implements ICache<K, V> {

    public static final byte MAP_INITIAL_CAPACITY = 16;
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractCaffeineCache.class);
    protected Map<String, Cache<K, V>> cacheSpace;
    protected Cache<K, V> kvCache;

    /**
     * 初始化
     */
    protected void initialCache() {
        /*
         * 缓存过期时间默认十分钟
         */
        this.kvCache = this.loadCache(600L);
        this.cacheSpace = new ConcurrentHashMap<>(16);
    }

    /**
     * 加载缓存对象
     *
     * @return cache object
     */
    protected Cache<K, V> loadCache(Long expired) {
        int processors = Runtime.getRuntime().availableProcessors();
        assert expired != null;
        return CacheBuilder.newBuilder()
                           .concurrencyLevel(processors)
                           .initialCapacity(10)
                           .maximumSize(100)
                           .expireAfterWrite(expired, TimeUnit.SECONDS)
                           .removalListener(removalNotification -> {
                               LOG.info(removalNotification.getKey() + " " + removalNotification.getValue() + " is remove!");
                           })
                           .build();
    }


    protected Cache<K, V> getObject() {
        // 继承子类没有给予初始化 则提供默认初始化
        if (kvCache == null) {
            synchronized (AbstractCaffeineCache.class) {
                if (kvCache == null) {
                    initialCache();
                }
            }
        }
        return this.kvCache;
    }
}
