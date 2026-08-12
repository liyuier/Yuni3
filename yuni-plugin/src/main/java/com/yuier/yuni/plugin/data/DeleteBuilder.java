package com.yuier.yuni.plugin.data;

import com.yuier.yuni.plugin.data.schema.TableSchema;

/**
 * 链式 DELETE 查询构造器。
 * 通过 {@code PluginUtils.delete(实体Class)} 获取实例。
 *
 * <pre>{@code
 * int rows = PluginUtils.delete(UserSteamId.class)
 *     .where("userId", 123L)
 *     .where("steamId", "XXX")
 *     .execute();
 * }</pre>
 */
public class DeleteBuilder<T> extends AbstractQueryBuilder<T> {

    DeleteBuilder(TableSchema schema, Class<T> entityClass) {
        super(schema, entityClass);
    }

    @Override
    public DeleteBuilder<T> where(String fieldName, Object value) {
        return super.where(fieldName, value);
    }

    /**
     * 执行删除。
     * @return 删除的行数
     */
    public int execute() {
        return PluginDataService.getInstance().getJdbi().withHandle(handle -> {
            var update = handle.createUpdate(buildSql());
            bindConditions(update);
            return update.execute();
        });
    }

    private String buildSql() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(quote(schema.getTableName()));
        appendWhere(sb);
        return sb.toString();
    }
}
