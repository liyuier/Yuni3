package com.yuier.yuni.plugin.data;

import com.yuier.yuni.plugin.data.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询构建器基类。持有 WHERE 条件链与 SQL 片段生成逻辑，
 * 供 SELECT（QueryBuilder）、DELETE（DeleteBuilder）、UPDATE（UpdateBuilder）共用。
 */
abstract class AbstractQueryBuilder<T> {

    final TableSchema schema;
    final Class<T> entityClass;
    final List<Condition> conditions = new ArrayList<>();

    AbstractQueryBuilder(TableSchema schema, Class<T> entityClass) {
        this.schema = schema;
        this.entityClass = entityClass;
    }

    /**
     * 添加等值条件。可多次调用以串联 AND 条件。
     * @param fieldName Java 字段名（camelCase），如 "userId"
     * @param value     字段值
     */
    @SuppressWarnings("unchecked")
    public <B extends AbstractQueryBuilder<T>> B where(String fieldName, Object value) {
        conditions.add(new Condition(fieldName, value));
        return (B) this;
    }

    /** 生成 WHERE 子句（不含 "WHERE" 关键字）。无条件时返回空串 */
    final String buildWhereClause() {
        if (conditions.isEmpty()) {
            return "";
        }
        return conditions.stream()
                .map(c -> quote(schema.findColumn(c.fieldName).getColumnName())
                        + " = :" + c.bindName)
                .collect(Collectors.joining(" AND "));
    }

    final void appendWhere(StringBuilder sb) {
        String where = buildWhereClause();
        if (!where.isEmpty()) {
            sb.append(" WHERE ").append(where);
        }
    }

    final void bindConditions(org.jdbi.v3.core.statement.SqlStatement<?> stmt) {
        for (Condition c : conditions) {
            stmt.bind(c.bindName, c.value);
        }
    }

    static String quote(String id) {
        return "\"" + id + "\"";
    }

    /** 单个等值条件 */
    static final class Condition {
        final String fieldName;
        final Object value;
        final String bindName;

        Condition(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
            this.bindName = fieldName + "_" + System.identityHashCode(this);
        }
    }
}
