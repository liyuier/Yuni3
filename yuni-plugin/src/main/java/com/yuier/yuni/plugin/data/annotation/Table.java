package com.yuier.yuni.plugin.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记实体类对应的数据库表。
 * 未指定 value 时自动从类名推导：ClassSimpleName → snake_case。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /** 表名。为空时自动推导 */
    String value() default "";
}
