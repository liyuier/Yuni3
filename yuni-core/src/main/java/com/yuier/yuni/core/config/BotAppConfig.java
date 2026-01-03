package com.yuier.yuni.core.config;

import com.yuier.yuni.core.model.bot.BotApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Title: BotAppConfig
 * @Author yuier
 * @Package com.yuier.yuni.event.model.config
 * @Date 2025/12/23 15:52
 * @description:
 */


@SpringBootConfiguration
public class BotAppConfig {

    @Value("${bot.app.command-flag:/}")
    private String commandFlag;

    @Value("${bot.app.sqlite-db-file:yuni.db}")
    private String sqliteDbFile;
    @Value("${bot.app.plugin.directory:yuni-application/plugins}")
    private String pluginDirectory;

    @Bean
    public BotApp botApp() {
        BotApp botApp = new BotApp();
        botApp.setCommandFlag(commandFlag);
        botApp.setPluginDirectory(pluginDirectory);
        botApp.setSqliteDbFile(sqliteDbFile);
        return botApp;
    }
}
