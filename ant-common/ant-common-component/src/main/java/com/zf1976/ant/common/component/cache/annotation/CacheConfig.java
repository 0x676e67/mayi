package com.zf1976.ant.common.component.cache.annotation;

import java.lang.annotation.*;

/**
 * 缓存配置类，设置当前代理类缓存命名空间
 *
 * @author mac
 * @date 2021/5/14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheConfig {


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
}
