package com.yuier.yuni.core.config;

import com.yuier.yuni.core.model.bot.BotModel;
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

    @Bean
    public BotModel bot() {
        BotModel botModel = new BotModel();
        botModel.setId(id);
        botModel.setNickName(nickName);
        botModel.setMasterId(masterId);
        return botModel;
    }
}
