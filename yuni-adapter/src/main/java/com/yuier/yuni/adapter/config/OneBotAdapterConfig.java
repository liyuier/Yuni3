package com.yuier.yuni.adapter.config;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotApiHttpClient;
import com.yuier.yuni.adapter.qq.http.OneBotHttpAdapter;
import com.yuier.yuni.adapter.qq.websocket.OneBotApiWsClient;
import com.yuier.yuni.adapter.qq.websocket.OneBotWsAdapter;
import com.yuier.yuni.adapter.qq.websocket.OneBotWsSessionStarter;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static com.yuier.yuni.adapter.qq.websocket.OneBotSessionIdConstance.ONEBOT_API_SOCKET_ID;

/**
 * @Title: OneBotAdapterConfig
 * @Author yuier
 * @Package com.yuier.yuni.adapter.config
 * @Date 2025/12/22 4:11
 * @description: OneBot 通信方式 adapter
 */

@SpringBootConfiguration
public class OneBotAdapterConfig {

    @Autowired
    OneBotDeserializer oneBotDeserializer;

    @Autowired
    OneBotApiHttpClient oneBotApiHttpClient;
    @Autowired
    OneBotSerialization oneBotSerialization;
    @Autowired
    OneBotWsSessionStarter oneBotWsSessionStarter;
    @Autowired
    YuniWebSocketManager yuniWebSocketManager;

    @Bean
    @ConditionalOnProperty(name = "onebot.communication.mode", havingValue = "http", matchIfMissing = true)
    public OneBotAdapter oneBotHttpAdapter() {
        return new OneBotHttpAdapter(oneBotDeserializer, oneBotSerialization, oneBotApiHttpClient);
    }

    @Bean
    @ConditionalOnProperty(name = "onebot.communication.mode", havingValue = "ws")
    public OneBotAdapter oneBotWsAdapter() {
        OneBotWsAdapter oneBotWsAdapter = new OneBotWsAdapter(oneBotDeserializer);
        oneBotWsSessionStarter.startOneBotApiSession();
        OneBotApiWsClient apiClient = new OneBotApiWsClient(yuniWebSocketManager.getWebSocket(ONEBOT_API_SOCKET_ID), oneBotDeserializer, oneBotSerialization);
        oneBotWsAdapter.setApiClient(apiClient);
        return oneBotWsAdapter;
    }
}
