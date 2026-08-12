package com.yuier.yuni.plugin.data;

import com.yuier.yuni.plugin.data.schema.ColumnDef;
import com.yuier.yuni.plugin.data.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 链式 UPDATE 查询构造器。
 * 通过 {@code PluginUtils.update(实体Class)} 获取实例。
 *
 * <pre>{@code
 * int rows = PluginUtils.update(UserSteamId.class)
 *     .set("steamId", "NEW_ID")
 *     .where("userId", 123L)
 *     .execute();
 * }</pre>
 */
public class UpdateBuilder<T> extends AbstractQueryBuilder<T> {

    private final List<SetValue> setValues = new ArrayList<>();

    UpdateBuilder(TableSchema schema, Class<T> entityClass) {
        super(schema, entityClass);
    }

    @Override
    public UpdateBuilder<T> where(String fieldName, Object value) {
        return super.where(fieldName, value);
    }

    /**
     * 设置要更新的字段值。可多次调用。
     * @param fieldName Java 字段名（camelCase）
     * @param value     新值
     */
    public UpdateBuilder<T> set(String fieldName, Object value) {
        setValues.add(new SetValue(fieldName, value));
        return this;
    }

    /**
     * 执行更新。
     * @return 更新的行数
     */
    public int execute() {
        return PluginDataService.getInstance().getJdbi().withHandle(handle -> {
            var update = handle.createUpdate(buildSql());
            for (SetValue sv : setValues) {
                update.bind(sv.bindName, sv.value);
            }
            bindConditions(update);
            return update.execute();
        });
    }

    private String buildSql() {
        if (setValues.isEmpty()) {
            throw new IllegalStateException("未设置任何更新字段，请先调用 set()");
        }
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(quote(schema.getTableName())).append(" SET ");
        sb.append(setValues.stream()
                .map(sv -> {
                    ColumnDef col = schema.findColumn(sv.fieldName);
                    return quote(col.getColumnName()) + " = :" + sv.bindName;
                })
                .collect(Collectors.joining(", ")));
        appendWhere(sb);
        return sb.toString();
    }

    private static final class SetValue {
        final String fieldName;
        final Object value;
        final String bindName;

        SetValue(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
            this.bindName = "set_" + fieldName + "_" + System.identityHashCode(this);
        }
    }
}
