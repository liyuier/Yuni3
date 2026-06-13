package com.yuier.yuni.webapi.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表数据查询响应。
 */
@Data
@AllArgsConstructor
public class TableDataResp {
    /** 列名列表 */
    private List<String> columns;
    /** 数据行，每行是列名→值的映射 */
    private List<Map<String, Object>> rows;
    /** 总行数 */
    private long total;
}
