package com.zf1976.ant.common.component.load.enums;

/**
 * mac
 * 2021/3/14
 *
 * @author
 * mac
 * */
public enum CacheRelation {

    /**
     * 默认,当清除缓存时候默认清除所有（REDIS,CAFFEINE）缓存
     */
    DEFAULT,
    /**
     * redis缓存实现
     */
    REDIS,
    /**
     * caffeine缓存实现
     */
    CAFFEINE;
}
