package com.yuier.yuni.webapi.service.database;

import com.yuier.yuni.webapi.dto.database.TableDataResp;
import com.yuier.yuni.webapi.dto.database.TableInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库查看服务。
 * 直接通过 JDBC 查询 SQLite 表结构与数据。
 */
@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 列出所有用户表。
     * @return 表信息列表
     */
    public List<TableInfo> listTables() {
        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name",
                String.class);

        List<TableInfo> result = new ArrayList<>();
        for (String name : tableNames) {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM \"" + name + "\"", Long.class);
            result.add(new TableInfo(name, count != null ? count : 0L));
        }
        return result;
    }

    /**
     * 分页查询表数据。
     * @param tableName 表名
     * @param page      页码
     * @param size      每页条数
     * @return 表数据
     */
    public TableDataResp queryTable(String tableName, int page, int size) {
        // 1. 获取列信息
        List<Map<String, Object>> pragmaRows = jdbcTemplate.queryForList(
                "PRAGMA table_info(\"" + tableName + "\")");
        List<String> columns = new ArrayList<>();
        for (Map<String, Object> row : pragmaRows) {
            columns.add((String) row.get("name"));
        }

        // 2. 查询数据
        int offset = (page - 1) * size;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM \"" + tableName + "\" LIMIT ? OFFSET ?", size, offset);

        // 3. 总数
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"" + tableName + "\"", Long.class);

        return new TableDataResp(columns, rows, total != null ? total : 0L);
    }
}
