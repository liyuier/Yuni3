package com.yuier.yuni.webapi.controller.database;

import com.yuier.yuni.webapi.dto.Result;
import com.yuier.yuni.webapi.dto.database.TableQueryReq;
import com.yuier.yuni.webapi.service.database.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据库查看接口。
 */
@RestController
@RequestMapping("/admin/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    /**
     * 获取数据库表列表
     * @return 表信息列表
     */
    @PostMapping("/tables")
    public Result<?> listTables() {
        return Result.ok(databaseService.listTables());
    }

    /**
     * 查询表数据
     * @param req 请求体，包含表名、页码、每页条数
     * @return 表数据
     */
    @PostMapping("/query")
    public Result<?> queryTable(@RequestBody TableQueryReq req) {
        if (req.getTableName() == null || req.getTableName().isBlank()) {
            return Result.fail(400, "表名不能为空");
        }
        return Result.ok(databaseService.queryTable(
                req.getTableName(),
                Math.max(1, req.getPage()),
                Math.max(1, Math.min(200, req.getSize()))
        ));
    }
}
