package com.yuier.yuni.event.model.config;

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

    @Bean
    public BotApp botApp() {
        BotApp botApp = new BotApp();
        botApp.setCommandFlag(commandFlag);
        return botApp;
    }
}
