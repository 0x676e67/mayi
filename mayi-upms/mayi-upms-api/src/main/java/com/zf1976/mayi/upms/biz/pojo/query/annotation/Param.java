package com.zf1976.mayi.upms.biz.pojo.query.annotation;

import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询类型注解，暂时不支持联表
 *
 * @author mac
 * @date 2020/10/26 11:44 下午
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    Type type() default Type.LIKE;

    String [] fields() default {};
}
