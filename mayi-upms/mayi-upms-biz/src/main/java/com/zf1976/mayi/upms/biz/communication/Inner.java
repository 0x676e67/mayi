package com.zf1976.mayi.upms.biz.communication;

import java.lang.annotation.*;

/**
 * @author mac
 * @date 2021/6/13
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Inner {
}
