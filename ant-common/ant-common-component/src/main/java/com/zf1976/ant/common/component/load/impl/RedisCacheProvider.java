package com.zf1976.ant.common.component.load.impl;

import com.power.common.util.CollectionUtil;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.property.CacheProperties;
import com.zf1976.ant.common.core.util.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author mac
 * @date 2021/3/14
 **/
public class RedisCacheProvider<K, V> implements ICache<K, V> {

    private final CacheProperties properties;
    private final RedisTemplate<Object, Map<Object, Object>> redisTemplate;

    public RedisCacheProvider(CacheProperties properties, RedisTemplate<Object, Map<Object, Object>> redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(String namespace, K key, Long expired, Supplier<V> supplier) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        if (kvMap == null) {
            kvMap = new ConcurrentHashMap<>(16);
        }
        V value = this.getValue(kvMap, key, supplier);
        this.saveCacheSpace(namespace, kvMap, expired);
        return value;
    }

    @Override
    public V get(String namespace, K key, Supplier<V> supplier) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        if (kvMap == null) {
            kvMap = new ConcurrentHashMap<>(16);
        }
        V value = this.getValue(kvMap, key, supplier);
        this.saveCacheSpace(namespace, kvMap);
        return value;
    }

    @Override
    public V get(String namespace, K key) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        if (kvMap == null) {
            kvMap = new ConcurrentHashMap<>(16);
        }
        this.saveCacheSpace(namespace, kvMap);
        return  kvMap.get(key);
    }

    @Override
    public void set(String namespace, K key, Long expired, V value) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        if (kvMap == null) {
            kvMap = new ConcurrentHashMap<>(16);
        }
        kvMap.put(key, value);
        this.saveCacheSpace(namespace, kvMap, expired);
    }

    @Override
    public void set(String namespace, K key, V value) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        if (kvMap == null) {
            kvMap = new ConcurrentHashMap<>(16);
        }
        kvMap.put(key, value);
        this.saveCacheSpace(namespace, kvMap);
    }

    @Override
    public void invalidate(String namespace) {
        this.redisTemplate.delete(this.keyFormatter(namespace));
    }

    @Override
    public void invalidate(String namespace, K key) {
        Map<K, V> kvMap = this.getCacheSpace(namespace);
        Long expire = this.redisTemplate.getExpire(this.keyFormatter(namespace), TimeUnit.MINUTES);
        if (kvMap != null && expire != null) {
            kvMap.remove(key);
            this.saveCacheSpace(namespace, kvMap, expire);
        }
    }

    @Override
    public void invalidateAll() {
        final Set<String> keys = RedisUtils.scanKeys(this.keyFormatter("*"));
        if (!CollectionUtils.isEmpty(keys)) {
            this.redisTemplate.delete(keys);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveCacheSpace(String namespace, Map<K, V> kvMap, Long expired) {
        this.redisTemplate.opsForValue()
                          .set(this.keyFormatter(namespace),
                                  (Map<Object, Object>) kvMap,
                                  expired == null || expired <0 ? properties.getExpireAlterWrite() : expired,
                                  TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    private void saveCacheSpace(String namespace, Map<K, V> kvMap) {
        this.redisTemplate.opsForValue()
                          .set(this.keyFormatter(namespace),
                                  (Map<Object, Object>) kvMap,
                                  properties.getExpireAlterWrite(),
                                  TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    private Map<K, V> getCacheSpace(String namespace) {
        return (Map<K, V>) this.redisTemplate.opsForValue().get(this.keyFormatter(namespace));
    }

    private String keyFormatter(String key) {
        return this.properties.getKeyPrefix() + key;
    }

    private V getValue(Map<K, V> kvMap, K k, Supplier<V> supplier) {
        V var1 = kvMap.get(k);
        if (var1 != null) {
            return var1;
        }
        V var2 = supplier.get();
        if (var2 != null) {
            kvMap.put(k, var2);
        }
        return var2;
    }
}
