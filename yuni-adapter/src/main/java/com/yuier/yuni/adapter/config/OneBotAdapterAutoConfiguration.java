package com.yuier.yuni.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.onebot.OneBotBot;
import com.yuier.yuni.adapter.onebot.OneBotProtocolHandler;
import com.yuier.yuni.adapter.onebot.transport.OneBotTransport;
import com.yuier.yuni.adapter.onebot.transport.http.OneBotHttpTransport;
import com.yuier.yuni.adapter.onebot.transport.ws.OneBotWsTransport;
import com.yuier.yuni.core.bot.JsonCodec;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.bot.YuniBotFactory;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: OneBotAdapterAutoConfiguration
 * @Author yuier
 * @Package com.yuier.yuni.adapter.config
 * @Date 2026/06/09
 * @description: OneBot 适配器自动配置。
 *               仅在 yuni.protocol.type = "onebot" 时激活。
 *               根据 yuni.protocol.onebot.mode 装配 HTTP 或 WS 传输层，
 *               将 YuniBotFactory 注册到 Spring 容器供 BotManager 发现和调用。
 */

@Configuration
@EnableConfigurationProperties(OneBotProperties.class)
@ConditionalOnProperty(name = "yuni.protocol.type", havingValue = "onebot")
public class OneBotAdapterAutoConfiguration {

    @Value("${bot.qq:0}")
    private String botId;

    @Bean
    public OneBotProtocolHandler oneBotProtocolHandler(ObjectMapper objectMapper, JsonCodec jsonCodec) {
        return new OneBotProtocolHandler(objectMapper, jsonCodec);
    }

    @Bean
    @ConditionalOnProperty(name = "yuni.protocol.onebot.mode", havingValue = "http", matchIfMissing = true)
    public OneBotHttpTransport oneBotHttpTransport(OneBotProperties properties, JsonCodec jsonCodec) {
        return new OneBotHttpTransport(properties, jsonCodec);
    }

    @Bean
    @ConditionalOnProperty(name = "yuni.protocol.onebot.mode", havingValue = "ws")
    public OneBotWsTransport oneBotWsTransport(OneBotProperties properties,
                                                JsonCodec jsonCodec,
                                                YuniWebSocketManager wsManager) {
        return new OneBotWsTransport(properties, jsonCodec, wsManager);
    }

    /**
     * YuniBotFactory — 系统通过此 SPI 创建 YuniBot。
     * BotManager 在启动时收集所有 YuniBotFactory，根据 yuni.protocol.type
     * 选择匹配的工厂，调用 createBot() 获取 bot 实例。
     */
    @Bean
    public YuniBotFactory oneBotYuniBotFactory(OneBotProperties properties,
                                                OneBotProtocolHandler protocolHandler,
                                                OneBotTransport transport,
                                                JsonCodec jsonCodec) {
        return new YuniBotFactory() {
            @Override
            public String getProtocolType() {
                return "onebot";
            }

            @Override
            public YuniBot createBot() {
                return new OneBotBot(properties, protocolHandler, transport, jsonCodec, botId);
            }
        };
    }
}
