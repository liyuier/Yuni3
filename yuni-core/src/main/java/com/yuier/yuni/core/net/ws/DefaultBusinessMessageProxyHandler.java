package com.yuier.yuni.core.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Title: DefaultBusinessMessageProxyHandler
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 15:04
 * @description: 业务相关 ws 事件处理器 BusinessMessageProxyHandler 的默认最简实现
 */

@Slf4j
public class DefaultBusinessMessageProxyHandler implements BusinessMessageProxyHandler {

    @Override
    public void handleMessage(String connectionId, String message) {
        log.info("[默认处理]连接 {} 收到业务消息: {}", connectionId, message);
        // 默认实现：简单打印消息
    }

    @Override
    public void onConnectionEstablished(String connectionId, WebSocketSession session) {
        log.info("[默认处理]连接 {} 已建立", connectionId);
        // 默认实现：打印连接信息
    }

    @Override
    public void onConnectionError(String connectionId, Throwable exception) {
        log.error("[默认处理]连接 {} 发生错误: {}", connectionId, exception.getMessage(), exception);
        // 默认实现：打印错误信息
    }

    @Override
    public void onConnectionClosed(String connectionId, CloseStatus status) {
        log.info("[默认处理]连接 {} 已关闭: {}", connectionId, status);
        // 默认实现：打印关闭信息
    }

    @Override
    public String getBusinessHeartBeatMessage() {
        return "";
    }
}
