package com.zf1976.ant.common.core.dev;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WINDOWS
 */
@Data
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

}
