package com.yuier.yuni.core.net.ws;

import com.yuier.yuni.core.net.ws.example.DefaultWsResponseWrapper;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Title: WebSocketHandler
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 15:01
 * @description: 自定义的通用 ws handler ，供 CommonWebSocketManager 使用
 */

@Slf4j
@Data
public class CommonWebSocketHandler extends TextWebSocketHandler {
    // 连接 ID
    @Getter
    private final String connectionId;
    // 是否已连接
    private final AtomicBoolean connected = new AtomicBoolean(false);
    // 定时任务执行器，用于执行心跳
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 当前会话
    private volatile WebSocketSession session;
    // ws 服务 URL
    private final String serverUrl;
    // 持有 ws manager ，用于重连操作
    private final CommonWebSocketManager webSocketManager;
    // 代理 handler ，外部传入，用于动态实现业务能力
    private final BusinessMessageProxyHandler businessHandler;
    // 用于包装响应
    private final WsResponseWrapper responseWrapper = new DefaultWsResponseWrapper();

    // 高级配置参数
    private final long heartbeatInterval; // 心跳间隔（毫秒）
    private final long reconnectInterval; // 重连间隔（毫秒）

    /**
     * 构造函数
     *
     * @param connectionId 连接ID
     * @param serverUrl 服务器URL
     * @param webSocketManager 管理器
     * @param businessHandler 业务处理器
     * @param heartbeatInterval 心跳间隔（毫秒），默认30000
     * @param reconnectInterval 重连间隔（毫秒），默认30000
     */
    public CommonWebSocketHandler(String connectionId, String serverUrl,
                                  CommonWebSocketManager webSocketManager,
                                  BusinessMessageProxyHandler businessHandler,
                                  Long heartbeatInterval,
                                  Long reconnectInterval) {
        this.connectionId = connectionId;
        this.serverUrl = serverUrl;
        this.webSocketManager = webSocketManager;
        this.businessHandler = businessHandler != null ? businessHandler : new DefaultBusinessMessageProxyHandler();
        this.heartbeatInterval = heartbeatInterval != null ? heartbeatInterval : 30000L;
        this.reconnectInterval = reconnectInterval != null ? reconnectInterval : 30000L;

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[连接建立]连接 {} 已建立: {}", connectionId, session.getId());
        this.session = session;
        connected.set(true);

        // 通知业务处理器连接已建立
        businessHandler.onConnectionEstablished(connectionId, session);

        // 启动心跳任务
        scheduler.scheduleAtFixedRate(this::sendHeartbeat,
                this.heartbeatInterval, this.heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("[收到消息]连接 {} 收到消息: {}", connectionId, payload);

        // 检查是否为心跳响应消息
        if ("pong".equals(payload)) {
            log.debug("[心跳响应]连接 {} 收到心跳响应", connectionId);
            return;
        }

        // 动态包装响应体并传递给业务处理器
        String wrappedMessage = wrapResponseMessage(payload);
        businessHandler.handleMessage(connectionId, wrappedMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[传输错误]连接 {} 传输错误: {}", connectionId, exception.getMessage(), exception);
        connected.set(false);
        businessHandler.onConnectionError(connectionId, exception);
        reconnect();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[连接关闭]连接 {} 已关闭: {}", connectionId, status);
        connected.set(false);
        businessHandler.onConnectionClosed(connectionId, status);
        reconnect();
    }

    /**
     * 发送消息到WebSocket服务器
     */
    public void sendMessage(String message) throws IOException {
        if (connected.get() && session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            throw new IOException("WebSocket连接未建立或已关闭");
        }
    }

    /**
     * 发送心跳消息
     */
    private void sendHeartbeat() {
        if (connected.get() && session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(businessHandler.getBusinessHeartBeatMessage()));
                log.debug("[心跳发送]连接 {} 发送心跳: ping", connectionId);
            } catch (IOException e) {
                log.error("[心跳失败]连接 {} 发送心跳失败: {}", connectionId, e.getMessage(), e);
                connected.set(false);
                reconnect();
            }
        }
    }

    /**
     * 重连逻辑
     */
    private void reconnect() {
        if (!connected.get()) {
            log.info("[重连尝试]连接 {} 尝试重新连接...", connectionId);
            // 延迟重连，使用配置的重连间隔
            scheduler.schedule(() -> {
                try {
                    log.info("[重连执行]连接 {} 重连任务执行", connectionId);

                    // 通过WebSocketManager重新启动连接
                    if (webSocketManager != null) {
                        // 先停止现有的连接（如果存在）
                        webSocketManager.stopConnection(connectionId);

                        // 重新创建连接，使用相同的配置
                        webSocketManager.createConnection(connectionId, serverUrl,
                                new CommonWebSocketHandler(connectionId, serverUrl, webSocketManager,
                                        businessHandler, heartbeatInterval, reconnectInterval));

                        // 启动新的连接
                        boolean started = webSocketManager.startConnection(connectionId);

                        if (started) {
                            log.info("[重连成功]连接 {} 重连成功", connectionId);
                        } else {
                            log.warn("[重连失败]连接 {} 重连失败", connectionId);
                        }
                    }
                } catch (Exception e) {
                    log.error("[重连异常]连接 {} 重连过程中发生异常: {}", connectionId, e.getMessage(), e);
                }
            }, this.reconnectInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 动态包装响应消息体
     */
    public String wrapResponseMessage(String originalMessage) {
        // 动态包装
        return responseWrapper.wrapRawJson(originalMessage);
    }

    @PreDestroy
    public void destroy() {
        log.info("[处理器销毁]连接 {} 的处理器被销毁", connectionId);
        scheduler.shutdown();
        connected.set(false);
    }

    public boolean isConnected() {
        return connected.get();
    }

}