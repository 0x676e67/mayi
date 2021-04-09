package com.zf1976.ant.common.component.load.annotation;

import com.zf1976.ant.common.component.load.enums.CacheImplement;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {

    /**
     * 缓存命名空间
     *
     * @return namespace
     */
    String namespace();

    /**
     * key
     *
     * @return key
     */
    String key() default "";

    /**
     * 动态命名空间缓存 根据用户分组
     *
     * @return /
     */
    boolean dynamics() default false;

    /**
     * 命名空间超时时间 - 单位:分钟
     *
     * @return time
     */
    long expired() default -1;

    /**
     * 缓存实现
     * @return relation
     */
    CacheImplement implement() default CacheImplement.REDIS;
}


