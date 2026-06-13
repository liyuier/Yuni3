package com.yuier.yuni.webapi.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 数据库表基本信息。
 */
@Data
@AllArgsConstructor
public class TableInfo {
    /** 表名 */
    private String name;
    /** 行数估算 */
    private long rowCount;
}
