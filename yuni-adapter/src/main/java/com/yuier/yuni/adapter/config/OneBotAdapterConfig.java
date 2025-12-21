package com.yuier.yuni.adapter.config;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotHttpAdapter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @Title: OneBotAdapterConfig
 * @Author yuier
 * @Package com.yuier.yuni.adapter.config
 * @Date 2025/12/22 4:11
 * @description: OneBot 通信方式 adapter
 */

@SpringBootConfiguration
public class OneBotAdapterConfig {

    @Bean
    @ConditionalOnProperty(name = "onebot.communication.mode", havingValue = "http", matchIfMissing = true)
    public OneBotAdapter oneBotHttpAdapter() {
        return new OneBotHttpAdapter();
    }
}
