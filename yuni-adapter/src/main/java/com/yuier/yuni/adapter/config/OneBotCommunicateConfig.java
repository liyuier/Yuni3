package com.yuier.yuni.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Title: OneBotCommunicateConfig
 * @Author yuier
 * @Package com.yuier.yuni.adapter.config
 * @Date 2025/12/29 17:41
 * @description:
 */

@SpringBootConfiguration
public class OneBotCommunicateConfig {

    @Value("${onebot.communication.mode}")
    private String mode;
    @Value("${onebot.communication.token}")
    private String token;
    @Value("${onebot.communication.ws.url}")
    private String wsUrl;
    @Value("${onebot.communication.ws.heartbeat-interval:30000}")
    private int wsHeartbeatInterval;
    @Value("${onebot.communication.http.url}")
    private String httpUrl;
    @Value("${onebot.communication.ws.timeout:3000}")
    private int wsTimeout;

    @Bean
    public OneBotCommunicate oneBotCommunicate() {
        OneBotCommunicate oneBotCommunicate = new OneBotCommunicate();
        oneBotCommunicate.setMode(mode);
        oneBotCommunicate.setToken(token);
        oneBotCommunicate.setWsUrl(wsUrl);
        oneBotCommunicate.setWsHeartbeatInterval(wsHeartbeatInterval);
        oneBotCommunicate.setHttpUrl(httpUrl);
        oneBotCommunicate.setWsTimeout(wsTimeout);
        return oneBotCommunicate;
    }
}
