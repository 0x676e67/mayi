package com.zf1976.ant.common.component.cache.annotation;

import com.zf1976.ant.common.component.cache.enums.CacheImplement;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

    /**
     * 缓存命名空间
     *
     * @return namespace
     */
    String namespace() default "";

    /**
     * 缓存依赖
     * @return /
     */
    String[] dependsOn() default {};

    /**
     * key
     *
     * @return key
     */
    String key() default "";

    /**
     * 支持清除后调用方法,只支持无参方法
     *
     * @return {@link String}
     */
    String[] postInvoke() default {};

    /**
     * 默认,当清除缓存时候默认清除所有（REDIS,CAFFEINE）缓存
     */
    boolean strategy() default true;

    /**
     * 缓存实现
     *
     * @return relation
     */
    CacheImplement implement() default CacheImplement.REDIS;
}
