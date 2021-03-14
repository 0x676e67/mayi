package com.zf1976.ant.common.component.load.annotation;

import com.zf1976.ant.common.component.load.enums.CacheRelation;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaffeineEvict {

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
     * 缓存实现
     * @return relation
     */
    CacheRelation relation() default CacheRelation.CAFFEINE;
}
