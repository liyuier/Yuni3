package com.yuier.yuni.webapi.dto.database;

import lombok.Data;

/**
 * 表数据查询请求。
 */
@Data
public class TableQueryReq {
    private String tableName;
    private int page = 1;
    private int size = 50;
}
