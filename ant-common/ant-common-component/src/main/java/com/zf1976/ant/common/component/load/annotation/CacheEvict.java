package com.zf1976.ant.common.component.load.annotation;

import com.zf1976.ant.common.component.load.enums.CacheImplement;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvict {

    /**
     * 缓存命名空间
     *
     * @return namespace
     */
    String namespace();

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
     * 默认,当清除缓存时候默认清除所有（REDIS,CAFFEINE）缓存
     */
    boolean strategy() default true;

    /**
     * 缓存实现
     * @return relation
     */
    CacheImplement implement() default CacheImplement.REDIS;
}
