package com.zf1976.ant.common.component.load;


import java.util.function.Supplier;

/**
 * @author WINDOWS
 */
public interface ICache<K, V> {

    /**
     * 记录namespace缓存的健
     */
    String RECORD_NAMESPACE_KEY = "record-namespace-key";

    /**
     * 获取缓存值，缓存空间不存在则根据expired 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V getValueAndSupplier(String namespace, K key, Long expired, Supplier<V> supplier);

    /**
     * 获取缓存值，缓存空间不存在则根据默认配置 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V getValueAndSupplier(String namespace, K key, Supplier<V> supplier);

    /**
     * 获取缓存值，不做处理
     *
     * @param namespace 缓存空间
     * @param key       key
     * @return V
     */
    V getValue(String namespace, K key);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param value     V
     */
    void setValue(String namespace, K key, V value, Long expired);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param value     V
     */
    void setValue(String namespace, K key, V value);

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

    /**
     * 命名空间记录
     *
     * @param namespace 命名空间
     */
    void recordNamespace(String namespace);

    /**
     * 强制格式化命名空间
     *
     * @param prefix 前缀
     * @param namespace 命名空间
     * @return {@link String}
     */
    String formatNamespace(String namespace);

}
