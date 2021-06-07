package com.zf1976.mayi.common.log.annotation;

import java.lang.annotation.*;

/**
 * 2020/12/24
 * @author mac
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 接口描述
     *
     * @return /
     */
    String description() default "";
}
