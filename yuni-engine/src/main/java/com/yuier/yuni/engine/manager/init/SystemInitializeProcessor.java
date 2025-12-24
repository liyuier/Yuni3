package com.yuier.yuni.engine.manager.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @Title: SystemInitializeProcessor
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/24 15:19
 * @description: 系统初始化
 */

@Component
@Slf4j
public class SystemInitializeProcessor {

    @Value("${bot.app.sqlite-db-file}")
    private String sqliteDbFile;

    public void checkDatabaseFile() throws IOException {
        // 检查数据库文件是否存在
        File dbFile = new File(sqliteDbFile);
        // 创建数据库文件
        if (!dbFile.exists()) {
            log.debug("数据库文件不存在，正在创建...");
            if (dbFile.createNewFile()) {
                log.info("创建数据库文件: " + dbFile.getAbsolutePath());
            } else {
                log.error("未找到数据库文件，且创建失败: " + dbFile.getAbsolutePath());
            }
        }
    }
}
