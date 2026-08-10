package com.yuier.yuni.plugin.data;

import java.util.List;

/**
 * 通用增删改查接口。
 * 通过调用 PluginUtils.repo(实体Class) 获取实例。
 *
 * @param <T> 实体类型，必须标注 @Table 且含 @Id 主键
 */
public interface CrudRepository<T> {

    /**
     * 保存实体。id 为 null 时执行 INSERT，否则执行 UPDATE。
     * @param entity 实体对象
     */
    void save(T entity);

    /**
     * 按主键查询。
     * @param id 主键值
     * @return 实体，不存在时返回 null
     */
    T findById(Object id);

    /**
     * 查询全部记录。
     * @return 实体列表，无记录时返回空列表
     */
    List<T> findAll();

    /**
     * 按主键删除。
     * @param id 主键值
     */
    void deleteById(Object id);

    /**
     * 按字段等值匹配删除。
     * @param fieldName Java 字段名（camelCase），如 "userId"
     * @param value     字段值
     */
    void deleteByField(String fieldName, Object value);

    /**
     * 按字段等值匹配查询。
     * @param fieldName Java 字段名（camelCase），如 "userId"
     * @param value     字段值
     * @return 匹配的实体列表，无记录时返回空列表
     */
    List<T> findByField(String fieldName, Object value);

    /**
     * 记录总数。
     * @return 表内记录数
     */
    long count();

    /**
     * 分页查询。
     * @param pageNum  页码，从 0 开始
     * @param pageSize 每页记录数
     * @return 实体列表
     */
    List<T> findPage(int pageNum, int pageSize);

    /**
     * 按主键判定是否存在。
     * @param id 主键值
     * @return 存在则 true
     */
    default boolean existsById(Object id) {
        return findById(id) != null;
    }
}
