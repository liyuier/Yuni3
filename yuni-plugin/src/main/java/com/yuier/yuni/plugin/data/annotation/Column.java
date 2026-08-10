package com.yuier.yuni.plugin.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式指定字段对应的数据库列名。
 * 未指定 value 时自动从字段名推导：camelCase → snake_case。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /** 列名。为空时自动推导 */
    String value() default "";
}
