package com.zf1976.ant.common.encrypt.annotation;

import java.lang.annotation.*;

/**
 * 开启接口加密
 *
 * @author mac
 * @date 2021/1/28
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableEncrypt {
}
