package com.zf1976.ant.common.component.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WINDOWS
 */
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    /**
     * 并发级别
     */
    private Integer concurrencyLevel;

    /**
     * 初始化容量
     */
    private Integer initialCapacity;

    /**
     * 最大容量
     */
    private Integer maximumSize;

    /**
     * 写入后过期时间 单位/seconds
     */
    private Long expireAlterWrite;

    /**
     * key前缀
     */
    private String keyPrefix;

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public CacheProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    public Integer getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public CacheProperties setConcurrencyLevel(Integer concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public Integer getInitialCapacity() {
        return initialCapacity;
    }

    public CacheProperties setInitialCapacity(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public CacheProperties setMaximumSize(Integer maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public Long getExpireAlterWrite() {
        return expireAlterWrite;
    }

    public CacheProperties setExpireAlterWrite(Long expireAlterWrite) {
        this.expireAlterWrite = expireAlterWrite;
        return this;
    }

    @Override
    public String toString() {
        return "CacheProperties{" +
                "concurrencyLevel=" + concurrencyLevel +
                ", initialCapacity=" + initialCapacity +
                ", maximumSize=" + maximumSize +
                ", expireAlterWrite=" + expireAlterWrite +
                ", keyPrefix='" + keyPrefix + '\'' +
                '}';
    }
}
