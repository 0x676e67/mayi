package com.zf1976.ant.common.component.load.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zf1976.ant.common.component.load.AbstractCaffeineCache;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.property.CacheProperties;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author WINDOWS
 */
public class CaffeineCacheProvider<K, V> extends AbstractCaffeineCache<K, V> {

    public CaffeineCacheProvider(CacheProperties properties) {
        super(properties);
        this.initialCache();
        this.checkStatus();
    }

    private void checkStatus() {
        Assert.notNull(super.kvCache, "Uninitialized!");
        Assert.notNull(super.cacheSpace, "Uninitialized!");
    }

    @Override
    protected void initialCache() {
        super.kvCache = this.loadCache(properties.getExpireAlterWrite());
        super.cacheSpace = new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
    }

    @Override
    protected Cache<K, V> loadCache(Long expired) {

        CacheBuilder<K, V> kvCacheBuilder = CacheBuilder.newBuilder()
                                                        .concurrencyLevel(properties.getConcurrencyLevel())
                                                        .initialCapacity(properties.getInitialCapacity())
                                                        .maximumSize(properties.getMaximumSize())
                                                        .removalListener(removalNotification -> {
                                                            if (LOG.isInfoEnabled()) {
                                                                LOG.info("key：{}---- value：{} is remove!", removalNotification.getKey(), removalNotification.getValue());
                                                            }
                                                        });

        if (expired == null || expired <= 0) {
            return Objects.requireNonNull(kvCacheBuilder.expireAfterWrite(properties.getExpireAlterWrite(), TimeUnit.MINUTES)
                                                        .build());
        }
        return Objects.requireNonNull(kvCacheBuilder.expireAfterWrite(Duration.ofMinutes(expired))
                                                    .build());
    }

    @Override
    public V get(String namespace, K key, Long expired, Supplier<V> supplier) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache == null) {
            kvCache = this.loadCache(expired);
            this.putNamespace(namespace, kvCache);
        }
        return this.getValue(kvCache, key, supplier);
    }

    @Override
    public V get(String namespace, K key, Supplier<V> supplier) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache == null) {
            kvCache = this.loadCache(properties.getExpireAlterWrite());
            this.putNamespace(namespace, kvCache);
        }
        return this.getValue(kvCache, key, supplier);
    }

    @Override
    public V get(String namespace, K key) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache == null) {
            kvCache = this.loadCache(properties.getExpireAlterWrite());
            this.putNamespace(namespace, kvCache);
        }
        return this.getDefaultValue(kvCache, key);
    }

    @Override
    public void set(String namespace, K key, Long expired, V value) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache == null) {
            kvCache = this.loadCache(expired);
            this.putNamespace(namespace, kvCache);
        }
        kvCache.put(key, value);
    }

    @Override
    public void set(String namespace, K key, V value) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache == null) {
            kvCache = this.loadCache(properties.getExpireAlterWrite());
            this.putNamespace(namespace, kvCache);
        }
        kvCache.put(key, value);
    }

    @Override
    public void invalidate(String namespace) {
        this.cacheSpace.remove(this.keyFormatter(namespace));
        this.removeNamespaceLog(namespace);
    }

    @Override
    public void invalidate(String namespace, K key) {
        Cache<K, V> kvCache = this.getCache(namespace);
        if (kvCache != null) {
            kvCache.invalidate(key);
            this.removeNamespaceKeyLog(namespace, key);
        }
    }

    @Override
    public void invalidateAll() {
        Iterator<Map.Entry<String, Cache<K, V>>> entryIterator = this.cacheSpace.entrySet().iterator();
        while (entryIterator.hasNext()) {
            entryIterator.remove();
        }
    }

    private Cache<K, V> getCache(String namespace) {
        return super.cacheSpace.get(this.keyFormatter(namespace));
    }

    private void putNamespace(String namespace, Cache<K, V> kvCache) {
        super.cacheSpace.put(this.keyFormatter(namespace), kvCache);
    }

    private V getValue(Cache<K, V> cache, K key, Supplier<V> supplier) {
        V var1 = this.getDefaultValue(cache, key);
        if (var1 != null) {
            return var1;
        }

        V var2 = supplier.get();
        if (var2 != null) {
            cache.put(key, var2);
        }
        return var2;
    }

    private V getDefaultValue(Cache<K, V> cache, K key) {
        assert cache != null;
        if (ObjectUtils.isEmpty(key)) {
            return null;
        }
        return cache.getIfPresent(key);
    }

    private void removeNamespaceLog(String namespace) {
        if (LOG.isInfoEnabled()) {
            LOG.info("the cache namespace:" + namespace + " has been destroyed");
        }
    }

    private void removeNamespaceKeyLog(String namespace, K key) {
        if (LOG.isInfoEnabled()) {
            LOG.info("the key：{} of the cached namespace：{} has been destroyed", namespace, key);
        }
    }

}
