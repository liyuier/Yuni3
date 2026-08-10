package com.yuier.yuni.plugin.data;

import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.data.schema.TableSchema;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件数据访问核心服务。
 * 管理全局 Jdbi 实例，负责实体注册、建表，并提供 Repository / QueryBuilder 实例。
 *
 * <p>插件在 {@code YuniPlugin.initialize()} 中调用 {@code registerEntity} 即可：</p>
 * <pre>{@code
 * PluginDataService dataService = SpringContextUtil.getBean(PluginDataService.class);
 * dataService.registerEntity(UserSteamId.class);
 * }</pre>
 */
@Slf4j
@Component
public class PluginDataService {

    private static volatile PluginDataService instance;

    @Getter
    private volatile Jdbi jdbi;

    /** 已注册的实体 Schema 缓存 */
    private final Map<Class<?>, TableSchema> schemas = new ConcurrentHashMap<>();

    /** 已创建的 Repository 缓存 */
    private final Map<Class<?>, CrudRepository<?>> repos = new ConcurrentHashMap<>();

    public PluginDataService() {
        instance = this;
    }

    /** 获取单例（供非 Spring 管理的类使用） */
    public static PluginDataService getInstance() {
        if (instance == null) {
            // 兜底：从 Spring 容器获取
            instance = SpringContextUtil.getBean(PluginDataService.class);
        }
        return instance;
    }

    // ---- 公开 API ----

    /**
     * 注册实体类并自动建表。
     * 一般放在插件 {@code initialize()} 中调用。
     *
     * @param entityClass 标注了 @Table 和 @Id 的实体类
     * @return 本次注册的 TableSchema
     */
    public TableSchema registerEntity(Class<?> entityClass) {
        // 防止重复注册
        if (schemas.containsKey(entityClass)) {
            log.debug("实体 {} 已注册，跳过", entityClass.getSimpleName());
            return schemas.get(entityClass);
        }

        ensureJdbi();
        TableSchema schema = new TableSchema(entityClass);
        schemas.put(entityClass, schema);

        // 自动建表
        String ddl = schema.buildCreateTableSql();
        log.info("创建/确认表: {}", schema.getTableName());
        jdbi.useHandle(handle -> handle.createUpdate(ddl).execute());

        log.info("实体 {} → 表 {} 注册完成，共 {} 个字段",
                entityClass.getSimpleName(), schema.getTableName(), schema.getColumns().size());
        return schema;
    }

    /**
     * 获取实体的 CrudRepository。
     * @param entityClass 已注册的实体类
     */
    @SuppressWarnings("unchecked")
    public <T> CrudRepository<T> getRepository(Class<T> entityClass) {
        TableSchema schema = getSchema(entityClass);
        return (CrudRepository<T>) repos.computeIfAbsent(entityClass,
                k -> new CrudRepositoryImpl<>(schema, entityClass));
    }

    /**
     * 获取实体的 QueryBuilder。
     * @param entityClass 已注册的实体类
     */
    public <T> QueryBuilder<T> createQueryBuilder(Class<T> entityClass) {
        TableSchema schema = getSchema(entityClass);
        return new QueryBuilder<>(schema, entityClass);
    }

    /**
     * 获取实体的 TableSchema。
     * @throws IllegalArgumentException 如果未注册
     */
    public TableSchema getSchema(Class<?> entityClass) {
        TableSchema schema = schemas.get(entityClass);
        if (schema == null) {
            throw new IllegalArgumentException(
                    "实体 " + entityClass.getSimpleName() + " 未注册，请先在插件的 initialize() 中调用 PluginDataService.registerEntity()");
        }
        return schema;
    }

    /**
     * 执行原始 SQL 查询。
     * @param sql         SQL 语句，参数使用 :name 占位
     * @param bindings    参数绑定，key 对应 SQL 中的 :name
     * @param resultClass 结果映射类型
     * @return 查询结果列表
     */
    public <T> List<T> rawQuery(String sql, Map<String, Object> bindings, Class<T> resultClass) {
        ensureJdbi();
        return jdbi.withHandle(handle -> {
            var query = handle.createQuery(sql);
            if (bindings != null) {
                bindings.forEach(query::bind);
            }
            return query.mapToBean(resultClass).list();
        });
    }

    /**
     * 执行原始 SQL 更新（INSERT / UPDATE / DELETE）。
     * @param sql      SQL 语句
     * @param bindings 参数绑定
     * @return 影响行数
     */
    public int rawUpdate(String sql, Map<String, Object> bindings) {
        ensureJdbi();
        return jdbi.withHandle(handle -> {
            var update = handle.createUpdate(sql);
            if (bindings != null) {
                bindings.forEach(update::bind);
            }
            return update.execute();
        });
    }

    // ---- 内部 ----

    private void ensureJdbi() {
        if (jdbi == null) {
            synchronized (this) {
                if (jdbi == null) {
                    String url = PluginUtils.getAppDatabaseUrl();
                    log.info("初始化插件数据库连接: {}", url);
                    jdbi = Jdbi.create(url);
                }
            }
        }
    }
}
