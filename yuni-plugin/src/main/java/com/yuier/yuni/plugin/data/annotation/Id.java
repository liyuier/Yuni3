package com.yuier.yuni.plugin.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记实体类的主键字段。对应 SQLite 的 INTEGER PRIMARY KEY AUTOINCREMENT。
 * 每个实体类必须有且仅有一个 @Id 字段。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
