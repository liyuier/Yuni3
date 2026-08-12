package com.yuier.yuni.plugin.data;

import com.yuier.yuni.plugin.data.schema.ColumnDef;
import com.yuier.yuni.plugin.data.schema.TableSchema;
import org.jdbi.v3.core.statement.Query;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 链式 SELECT 查询构造器。
 * 通过 {@code PluginUtils.query(实体Class)} 获取实例。
 *
 * <pre>{@code
 * List<UserSteamId> result = PluginUtils.query(UserSteamId.class)
 *     .where("userId", 123L)
 *     .orderBy("id", "DESC")
 *     .limit(10)
 *     .list();
 * }</pre>
 */
public class QueryBuilder<T> extends AbstractQueryBuilder<T> {

    private String orderByColumn;
    private String orderDirection = "ASC";
    private Integer limit;
    private Integer offset;

    QueryBuilder(TableSchema schema, Class<T> entityClass) {
        super(schema, entityClass);
    }

    @Override
    public QueryBuilder<T> where(String fieldName, Object value) {
        return super.where(fieldName, value);
    }

    /** 添加排序。 */
    public QueryBuilder<T> orderBy(String fieldName, String direction) {
        this.orderByColumn = fieldName;
        this.orderDirection = direction;
        return this;
    }

    /** 限制返回行数。 */
    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /** 偏移量，常与 limit 配合分页。 */
    public QueryBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    /** 执行查询，返回列表。 */
    public List<T> list() {
        return PluginDataService.getInstance().getJdbi().withHandle(handle -> {
            Query query = handle.createQuery(buildSql());
            bindConditions(query);
            return query.mapToBean(entityClass).list();
        });
    }

    /** 执行查询，返回单个结果。无结果时返回 null，多结果时返回第一条。 */
    public T single() {
        List<T> list = limit(1).list();
        return list.isEmpty() ? null : list.get(0);
    }

    /** 执行查询，返回结果数量。 */
    public long count() {
        return PluginDataService.getInstance().getJdbi().withHandle(handle -> {
            Query query = handle.createQuery(buildCountSql());
            bindConditions(query);
            return query.mapTo(Long.class).one();
        });
    }

    // ---- SQL 构建 ----

    private String buildSql() {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(schema.getColumns().stream()
                .map(c -> quote(c.getColumnName()) + " AS " + quote(c.getField().getName()))
                .collect(Collectors.joining(", ")));
        sb.append(" FROM ").append(quote(schema.getTableName()));
        appendWhere(sb);
        if (orderByColumn != null) {
            ColumnDef col = schema.findColumn(orderByColumn);
            sb.append(" ORDER BY ").append(quote(col.getColumnName()))
                    .append(" ").append(orderDirection);
        }
        if (limit != null) {
            sb.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sb.append(" OFFSET ").append(offset);
        }
        return sb.toString();
    }

    private String buildCountSql() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ");
        sb.append(quote(schema.getTableName()));
        appendWhere(sb);
        return sb.toString();
    }
}
