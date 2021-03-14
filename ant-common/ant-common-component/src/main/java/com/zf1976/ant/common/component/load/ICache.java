package com.zf1976.ant.common.component.load;


import com.google.common.cache.Cache;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.property.CacheProperties;

import java.util.function.Supplier;

/**
 * @author WINDOWS
 */
public interface ICache<K, V> {


    /**
     * 缓存配置
     */
    CacheProperties CACHE_PROPERTY_CONFIG = ApplicationConfigUtils.getCacheProperties();

    /**
     * 获取缓存值，缓存空间不存在则根据expired 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V get(String namespace, K key, Long expired, Supplier<V> supplier);

    /**
     * 获取缓存值，缓存空间不存在则根据默认配置 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V get(String namespace, K key, Supplier<V> supplier);

    /**
     * 获取缓存值，不做处理
     *
     * @param namespace 缓存空间
     * @param key       key
     * @return V
     */
    V get(String namespace, K key);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param value     V
     */
    void set(String namespace, K key, Long expired, V value);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param value     V
     */
    void set(String namespace, K key, V value);

    /**
     * 根据命名空间删除缓存
     *
     * @param namespace 缓存空间
     */
    void invalidate(String namespace);

    /**
     * 使某缓存空间某缓存失效
     *
     * @param namespace 缓存空间
     * @param key       key
     */
    void invalidate(String namespace, K key);

    /**
     * 清除所有缓存
     */
    void invalidateAll();


}
