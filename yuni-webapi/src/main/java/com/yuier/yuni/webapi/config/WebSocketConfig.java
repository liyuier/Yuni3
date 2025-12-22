package com.yuier.yuni.webapi.config;

import com.yuier.yuni.webapi.websocket.YuniWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Title: WebSocketConfig
 * @Author yuier
 * @Package com.yuier.yuni.webapi.config
 * @Date 2025/12/22 19:31
 * @description: ws 配置类
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new YuniWebSocketHandler(), "")
                .setAllowedOrigins("*");
    }
}
