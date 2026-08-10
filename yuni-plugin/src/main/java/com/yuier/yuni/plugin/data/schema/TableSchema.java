package com.yuier.yuni.plugin.data.schema;

import com.yuier.yuni.plugin.data.annotation.Column;
import com.yuier.yuni.plugin.data.annotation.Id;
import com.yuier.yuni.plugin.data.annotation.Table;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实体类的表结构元数据。
 * 从 @Table / @Id / @Column 注解解析得到，缓存在 PluginDataService 中。
 */
@Getter
public class TableSchema {

    private final Class<?> entityClass;
    private final String tableName;
    private final ColumnDef primaryKey;
    private final List<ColumnDef> columns;            // 全部列（含主键）
    private final List<ColumnDef> dataColumns;        // 非主键列（用于 INSERT/UPDATE）

    public TableSchema(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.tableName = resolveTableName(entityClass);

        List<ColumnDef> allColumns = new ArrayList<>();
        ColumnDef pk = null;

        for (Field field : entityClass.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                    || java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            boolean isPk = field.isAnnotationPresent(Id.class);
            String colName = resolveColumnName(field);
            ColumnDef col = new ColumnDef(field, colName, isPk);
            allColumns.add(col);
            if (isPk) {
                pk = col;
            }
        }

        if (pk == null) {
            throw new IllegalArgumentException(
                    entityClass.getName() + " 缺少 @Id 主键注解，无法注册为数据库实体");
        }

        this.primaryKey = pk;
        this.columns = List.copyOf(allColumns);
        this.dataColumns = allColumns.stream()
                .filter(c -> !c.isPrimaryKey())
                .collect(Collectors.toUnmodifiableList());
    }

    // ---- DDL / DML 生成 ----

    /** CREATE TABLE IF NOT EXISTS ... */
    public String buildCreateTableSql() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(quote(tableName)).append(" (");
        sb.append(columns.stream()
                .map(this::columnDdl)
                .collect(Collectors.joining(", ")));
        sb.append(")");
        return sb.toString();
    }

    private String columnDdl(ColumnDef col) {
        String ddl = quote(col.getColumnName()) + " " + col.getSqlType();
        if (col.isPrimaryKey()) {
            ddl += " PRIMARY KEY AUTOINCREMENT";
        }
        return ddl;
    }

    /** INSERT INTO table (col1, col2) VALUES (:field1, :field2) */
    public String buildInsertSql() {
        String cols = dataColumns.stream()
                .map(c -> quote(c.getColumnName()))
                .collect(Collectors.joining(", "));
        String vals = dataColumns.stream()
                .map(c -> ":" + c.getField().getName())
                .collect(Collectors.joining(", "));
        return "INSERT INTO " + quote(tableName) + " (" + cols + ") VALUES (" + vals + ")";
    }

    /** UPDATE table SET col1 = :field1, ... WHERE pkCol = :pkField */
    public String buildUpdateSql() {
        String sets = dataColumns.stream()
                .map(c -> quote(c.getColumnName()) + " = :" + c.getField().getName())
                .collect(Collectors.joining(", "));
        return "UPDATE " + quote(tableName) + " SET " + sets
                + " WHERE " + quote(primaryKey.getColumnName()) + " = :" + primaryKey.getField().getName();
    }

    /** SELECT * FROM table — 带别名确保 camelCase 映射 */
    public String buildSelectAllSql() {
        return "SELECT " + selectColumns() + " FROM " + quote(tableName);
    }

    /** SELECT ... FROM table WHERE pkCol = :pkField */
    public String buildSelectByIdSql() {
        return "SELECT " + selectColumns() + " FROM " + quote(tableName)
                + " WHERE " + quote(primaryKey.getColumnName()) + " = :" + primaryKey.getField().getName();
    }

    /** DELETE FROM table WHERE pkCol = :pkField */
    public String buildDeleteByIdSql() {
        return "DELETE FROM " + quote(tableName)
                + " WHERE " + quote(primaryKey.getColumnName()) + " = :" + primaryKey.getField().getName();
    }

    /** DELETE FROM table WHERE col = :value */
    public String buildDeleteByFieldSql(String fieldName) {
        ColumnDef col = requireColumn(fieldName);
        return "DELETE FROM " + quote(tableName)
                + " WHERE " + quote(col.getColumnName()) + " = :value";
    }

    /** SELECT ... FROM table WHERE col = :value */
    public String buildSelectByFieldSql(String fieldName) {
        ColumnDef col = requireColumn(fieldName);
        return "SELECT " + selectColumns() + " FROM " + quote(tableName)
                + " WHERE " + quote(col.getColumnName()) + " = :value";
    }

    /** SELECT COUNT(*) FROM table */
    public String buildCountSql() {
        return "SELECT COUNT(*) FROM " + quote(tableName);
    }

    /** SELECT ... FROM table LIMIT :limit OFFSET :offset */
    public String buildPageSql() {
        return "SELECT " + selectColumns() + " FROM " + quote(tableName)
                + " LIMIT :limit OFFSET :offset";
    }

    // ---- 工具方法 ----

    /** 根据字段名查找 ColumnDef */
    public ColumnDef findColumn(String fieldName) {
        for (ColumnDef c : columns) {
            if (c.getField().getName().equals(fieldName)) {
                return c;
            }
        }
        throw new IllegalArgumentException("实体 " + entityClass.getSimpleName() + " 中不存在字段: " + fieldName);
    }

    /** 根据字段名查找 ColumnDef，不存在则抛异常 */
    public ColumnDef requireColumn(String fieldName) {
        return findColumn(fieldName);
    }

    // ---- private helpers ----

    private String selectColumns() {
        return columns.stream()
                .map(c -> quote(c.getColumnName()) + " AS " + quote(c.getField().getName()))
                .collect(Collectors.joining(", "));
    }

    static String resolveTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !table.value().isBlank()) {
            return table.value();
        }
        return camelToSnake(entityClass.getSimpleName());
    }

    static String resolveColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.value().isBlank()) {
            return column.value();
        }
        return camelToSnake(field.getName());
    }

    static String camelToSnake(String camel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char ch = camel.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static String quote(String identifier) {
        // SQLite 使用双引号或方括号或反引号均可，这里用双引号
        return "\"" + identifier + "\"";
    }
}
