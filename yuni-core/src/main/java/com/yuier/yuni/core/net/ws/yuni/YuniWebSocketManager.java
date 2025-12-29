package com.yuier.yuni.core.net.ws.yuni;

import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: YuniWebSocketManager
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2025/12/29 1:55
 * @description: 基于 OkHttp 封装的 ws 管理器
 */

@Component
public class YuniWebSocketManager {

    private Map<String, YuniWebSocketConnector> webSocketConnectorMap = new ConcurrentHashMap<>();

    /**
     * 启动新的连接
     * @param connectionId  业务定义的连接 ID
     * @param connector  连接管理器
     */
    public void startNewConnection(String connectionId, YuniWebSocketConnector connector) {
        WebSocket webSocket = connector.startConnection();
        webSocketConnectorMap.put(connectionId, connector);
    }

    /**
     * 重启连接
     * @param connectionId  连接 ID
     */
    public void restartConnection(String connectionId) {
        YuniWebSocketConnector connector = webSocketConnectorMap.get(connectionId);
        if (connector != null) {
            WebSocket webSocket = connector.startConnection();
            webSocketConnectorMap.put(connectionId, connector);
        }
    }

    /**
     * 根据 ID 获取连接管理器
     * @param connectionId  连接 ID
     * @return  连接管理器
     */
    public YuniWebSocketConnector getWebSocket(String connectionId) {
        return webSocketConnectorMap.get(connectionId);
    }
}
