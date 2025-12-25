package com.yuier.yuni.core.net.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Title: BusinessMessageProxyHandler
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 15:02
 * @description: 自定义业务相关的 ws 事件处理接口，供 CommonWebSocketHandler 使用
 */

public interface BusinessMessageProxyHandler {
    /**
     * 处理接收到的消息
     */
    void handleMessage(String connectionId, String message);

    /**
     * 连接建立时的回调
     */
    void onConnectionEstablished(String connectionId, WebSocketSession session);

    /**
     * 连接错误时的回调
     */
    void onConnectionError(String connectionId, Throwable exception);

    /**
     * 连接关闭时的回调
     */
    void onConnectionClosed(String connectionId, CloseStatus status);
}