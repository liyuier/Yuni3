package com.yuier.yuni.plugin.data.schema;

import lombok.Getter;

import java.lang.reflect.Field;

/**
 * 单个列的定义：持有 Field 引用，缓存列名与 SQL 类型。
 */
@Getter
public class ColumnDef {

    /** Java 字段引用 */
    private final Field field;

    /** 数据库列名（snake_case 或显式指定） */
    private final String columnName;

    /** SQL 类型字面量，如 "INTEGER"、"TEXT"、"REAL"、"BLOB" */
    private final String sqlType;

    /** 是否为主键 */
    private final boolean primaryKey;

    public ColumnDef(Field field, String columnName, boolean primaryKey) {
        this.field = field;
        this.columnName = columnName;
        this.primaryKey = primaryKey;
        this.sqlType = mapJavaTypeToSql(field);
        // 方便反射访问私有字段
        field.setAccessible(true);
    }

    // ---- 类型映射 ----

    private static String mapJavaTypeToSql(Field field) {
        Class<?> type = field.getType();
        if (type == Long.class || type == long.class
                || type == Integer.class || type == int.class
                || type == Short.class || type == short.class
                || type == Boolean.class || type == boolean.class) {
            return "INTEGER";
        }
        if (type == String.class) {
            return "TEXT";
        }
        if (type == Double.class || type == double.class
                || type == Float.class || type == float.class) {
            return "REAL";
        }
        if (type == byte[].class) {
            return "BLOB";
        }
        throw new IllegalArgumentException(
                "不支持的字段类型: " + type.getName() + " (字段: " + field.getName() + ")");
    }
}
