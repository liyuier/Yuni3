package com.yuier.yuni.plugin.data;

import com.yuier.yuni.plugin.data.schema.ColumnDef;
import com.yuier.yuni.plugin.data.schema.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Query;

import java.util.List;

/**
 * CrudRepository 的通用实现。
 * 通过 JDBI Handle 执行动态生成的 SQL，使用 bindBean / mapToBean 与实体交互。
 */
@Slf4j
public class CrudRepositoryImpl<T> implements CrudRepository<T> {

    private final TableSchema schema;
    private final Class<T> entityClass;

    public CrudRepositoryImpl(TableSchema schema, Class<T> entityClass) {
        this.schema = schema;
        this.entityClass = entityClass;
    }

    // ---- CRUD ----

    @Override
    public void save(T entity) {
        Object id = getPkValue(entity);
        if (id == null) {
            insert(entity);
        } else {
            // 如果主键值对应的记录不存在则 insert，否则 update
            if (existsById(id)) {
                update(entity);
            } else {
                insert(entity);
            }
        }
    }

    @Override
    public T findById(Object id) {
        return executeQuery(handle -> {
            Query query = handle.createQuery(schema.buildSelectByIdSql());
            query.bind(schema.getPrimaryKey().getField().getName(), id);
            List<T> list = query.mapToBean(entityClass).list();
            return list.isEmpty() ? null : list.get(0);
        });
    }

    @Override
    public List<T> findAll() {
        return executeQuery(handle ->
                handle.createQuery(schema.buildSelectAllSql())
                        .mapToBean(entityClass)
                        .list());
    }

    @Override
    public void deleteById(Object id) {
        executeUpdate(handle ->
                handle.createUpdate(schema.buildDeleteByIdSql())
                        .bind(schema.getPrimaryKey().getField().getName(), id)
                        .execute());
    }

    @Override
    public void deleteByField(String fieldName, Object value) {
        executeUpdate(handle ->
                handle.createUpdate(schema.buildDeleteByFieldSql(fieldName))
                        .bind("value", value)
                        .execute());
    }

    @Override
    public List<T> findByField(String fieldName, Object value) {
        return executeQuery(handle ->
                handle.createQuery(schema.buildSelectByFieldSql(fieldName))
                        .bind("value", value)
                        .mapToBean(entityClass)
                        .list());
    }

    @Override
    public long count() {
        return executeQuery(handle ->
                handle.createQuery(schema.buildCountSql())
                        .mapTo(Long.class)
                        .one());
    }

    @Override
    public List<T> findPage(int pageNum, int pageSize) {
        return executeQuery(handle ->
                handle.createQuery(schema.buildPageSql())
                        .bind("limit", pageSize)
                        .bind("offset", pageNum * pageSize)
                        .mapToBean(entityClass)
                        .list());
    }

    // ---- internal ----

    private void insert(T entity) {
        executeUpdate(handle -> {
            handle.createUpdate(schema.buildInsertSql())
                    .bindBean(entity)
                    .execute();
            // 回填自增主键
            Long generatedId = handle.createQuery("SELECT last_insert_rowid()")
                    .mapTo(Long.class).one();
            setPkValue(entity, generatedId);
            return null;
        });
    }

    private void update(T entity) {
        executeUpdate(handle ->
                handle.createUpdate(schema.buildUpdateSql())
                        .bindBean(entity)
                        .execute());
    }

    // ---- Jdbi 执行模板 ----

    /** 需要 Jdbi 实例，从 PluginDataService 持有的单例获取 */
    private <R> R executeQuery(java.util.function.Function<Handle, R> fn) {
        return getJdbi().withHandle(handle -> fn.apply(handle));
    }

    private void executeUpdate(java.util.function.Function<Handle, Integer> fn) {
        getJdbi().useHandle(handle -> fn.apply(handle));
    }

    private org.jdbi.v3.core.Jdbi getJdbi() {
        return PluginDataService.getInstance().getJdbi();
    }

    // ---- 反射辅助 ----

    private Object getPkValue(T entity) {
        try {
            return schema.getPrimaryKey().getField().get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法读取主键值", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void setPkValue(T entity, Object value) {
        try {
            schema.getPrimaryKey().getField().set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法设置主键值", e);
        }
    }
}
