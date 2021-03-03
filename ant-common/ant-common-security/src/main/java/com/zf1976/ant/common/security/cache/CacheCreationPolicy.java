package com.zf1976.ant.common.security.cache;

/**
 * mac
 * 2020/11/28
 *
 * @author mac*/
public enum CacheCreationPolicy {

    /**
     * redis cache 策略
     */
    REDIS,

    /**
     * local cache 策略
     */
    LOCAL;

    CacheCreationPolicy() {}

}
