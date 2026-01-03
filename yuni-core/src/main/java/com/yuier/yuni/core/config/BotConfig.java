package com.yuier.yuni.core.config;

import com.yuier.yuni.core.model.bot.Bot;
import com.yuier.yuni.core.model.bot.BotApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Title: BotConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/24 3:40
 * @description: 机器人实体类配置类
 */

@SpringBootConfiguration
public class BotConfig {

    @Value("${bot.qq}")
    private Long id;
    @Value("${bot.nick-name:yuni}")
    private String nickName;
    @Value("${bot.master-qq}")
    private Long masterId;

    @Autowired
    private BotApp botApp;

    @Bean
    public Bot bot() {
        Bot bot = new Bot();
        bot.setId(id);
        bot.setNickName(nickName);
        bot.setMasterId(masterId);
        bot.setBotApp(botApp);
        return bot;
    }
}
