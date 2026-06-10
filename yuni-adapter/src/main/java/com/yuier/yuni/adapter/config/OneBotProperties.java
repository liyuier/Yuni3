package com.yuier.yuni.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Title: OneBotProperties
 * @Author yuier
 * @Package com.yuier.yuni.adapter.config
 * @Date 2026/06/09
 * @description: OneBot 通信配置属性，使用 Spring Boot 类型安全绑定。
 *               由 @EnableConfigurationProperties 注册，无需 @Component。
 */

@Data
@ConfigurationProperties(prefix = "yuni.protocol.onebot")
public class OneBotProperties {

    /** 通信模式：ws（WebSocket）或 http */
    private String mode = "http";

    /** 认证 Token */
    private String token;

    /** WebSocket 配置 */
    private WsConfig ws = new WsConfig();

    /** HTTP 配置 */
    private HttpConfig http = new HttpConfig();

    @Data
    public static class WsConfig {
        /** WebSocket 连接地址 */
        private String url = "ws://localhost:3001";
        /** 心跳间隔（毫秒） */
        private int heartbeatInterval = 30000;
        /** 超时时间（毫秒） */
        private int timeout = 3000;
        /** 断联后等待重连的最大秒数 */
        private int reconnectWaitSeconds = 10;
    }

    @Data
    public static class HttpConfig {
        /** HTTP 请求连接地址 */
        private String url = "http://localhost:3000";
    }

    /** 重试配置 */
    private RetryConfig retry = new RetryConfig();

    @Data
    public static class RetryConfig {
        /** 业务层最大重试次数（不含首次调用，0=不重试） */
        private int maxRetries = 2;
        /** 重试退避基数（毫秒），第 n 次重试等待 backoffBaseMs * n 毫秒 */
        private int backoffBaseMs = 500;
    }

    // ---- 兼容旧代码的便捷方法 ----

    public String getWsUrl() {
        String url = ws.getUrl();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String getHttpUrl() {
        String url = http.getUrl();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public int getWsHeartbeatInterval() {
        return ws.getHeartbeatInterval();
    }

    public int getWsTimeout() {
        return ws.getTimeout();
    }

    public int getWsReconnectWaitSeconds() {
        return ws.getReconnectWaitSeconds();
    }

    public int getMaxRetries() {
        return retry.getMaxRetries();
    }

    public int getRetryBackoffBaseMs() {
        return retry.getBackoffBaseMs();
    }
}
