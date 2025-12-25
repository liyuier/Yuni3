package com.yuier.yuni.core.net.ws;

import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: CommonWebSocketManager
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 13:58
 * @description: WS 连接管理器
 * 通用WebSocket连接管理器
 * 负责管理多个WebSocket连接的生命周期
 * 该类为通用组件，业务无关，一般不需要修改
 */

@Data
@Component
@Slf4j
public class CommonWebSocketManager {

    // 存储所有连接管理器实例，使用连接ID作为键
    private final Map<String, WebSocketConnectionManager> connectionManagers = new ConcurrentHashMap<>();

    // 存储连接状态
    private final Map<String, Boolean> connectionStatus = new ConcurrentHashMap<>();

    // 存储WebSocket处理器
    private final Map<String, WebSocketHandler> webSocketHandlers = new ConcurrentHashMap<>();

    /**
     * 创建并配置新的WebSocket连接
     *
     * @param connectionId 连接的唯一标识符
     * @param serverUrl    WebSocket服务器地址
     * @param handler      消息处理器
     * @return 是否成功创建连接配置
     */
    public boolean createConnection(String connectionId, String serverUrl, WebSocketHandler handler) {
        // 检查连接是否已存在
        if (connectionManagers.containsKey(connectionId)) {
            log.warn("尝试创建已存在的连接: {}", connectionId);
            return false;
        }

        // 创建新的连接管理器
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                handler,
                serverUrl
        );

        // 设置为手动启动（不自动启动）
        manager.setAutoStartup(false);

        // 保存连接管理器
        connectionManagers.put(connectionId, manager);
        connectionStatus.put(connectionId, false);
        // 保存 WebSocket 处理器
        webSocketHandlers.put(connectionId, handler);

        log.info("WebSocket连接配置完成: {}, URL: {}", connectionId, serverUrl);
        return true;
    }

    /**
     * 设置连接的认证令牌
     * @param connectionId 连接的唯一标识符
     * @param authToken 认证令牌
     */
    public void setAuthToken(String connectionId, String authToken) {
        WebSocketConnectionManager manager = connectionManagers.get(connectionId);
        if (manager == null) {
            log.error("尝试设置不存在的连接的认证令牌: {}", connectionId);
            return;
        }

        HttpHeaders headers = manager.getHeaders();
        headers.add("Authorization", "Bearer " + authToken);
    }

    /**
     * 启动指定的WebSocket连接
     *
     * @param connectionId 连接的唯一标识符
     * @return 是否成功启动连接
     */
    public boolean startConnection(String connectionId) {
        WebSocketConnectionManager manager = connectionManagers.get(connectionId);
        if (manager == null) {
            log.error("尝试启动不存在的连接: {}", connectionId);
            return false;
        }

        if (connectionStatus.get(connectionId)) {
            log.warn("连接已在运行: {}", connectionId);
            return false;
        }

        try {
            manager.start();
            connectionStatus.put(connectionId, true);
            log.info("WebSocket连接已启动: {}", connectionId);
            return true;
        } catch (Exception e) {
            log.error("启动连接失败: {}, 错误: {}", connectionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 停止指定的WebSocket连接
     *
     * @param connectionId 连接的唯一标识符
     * @return 是否成功停止连接
     */
    public boolean stopConnection(String connectionId) {
        WebSocketConnectionManager manager = connectionManagers.get(connectionId);
        if (manager == null) {
            log.error("尝试停止不存在的连接: {}", connectionId);
            return false;
        }

        if (!connectionStatus.get(connectionId)) {
            log.warn("尝试停止未运行的连接: {}", connectionId);
            return false;
        }

        try {
            manager.stop();
            connectionStatus.put(connectionId, false);
            log.info("WebSocket连接已停止: {}", connectionId);
            return true;
        } catch (Exception e) {
            log.error("停止连接失败: {}, 错误: {}", connectionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查连接状态
     *
     * @param connectionId 连接的唯一标识符
     * @return 连接是否正在运行
     */
    public boolean isConnectionRunning(String connectionId) {
        Boolean status = connectionStatus.get(connectionId);
        return status != null && status;
    }

    /**
     * 获取所有连接的ID列表
     *
     * @return 连接ID列表
     */
    public List<String> getAllConnections() {
        List<String> connections = new ArrayList<>(connectionManagers.keySet());
        log.debug("获取所有连接列表，当前连接数: {}", connections.size());
        return connections;
    }

    /**
     * 移除指定的连接（停止并清理）
     *
     * @param connectionId 连接的唯一标识符
     * @return 是否成功移除连接
     */
    public boolean removeConnection(String connectionId) {
        if (isConnectionRunning(connectionId)) {
            stopConnection(connectionId);
        }

        connectionManagers.remove(connectionId);
        connectionStatus.remove(connectionId);
        log.info("WebSocket连接已移除: {}", connectionId);
        return true;
    }

    /**
     * 获取连接管理器
     */
    public WebSocketConnectionManager getConnectionManager(String connectionId) {
        return connectionManagers.get(connectionId);
    }

    /**
     * 应用关闭时清理所有连接
     */
    @PreDestroy
    public void cleanup() {
        log.info("开始清理所有WebSocket连接...");
        int cleanupCount = 0;
        for (String connectionId : connectionManagers.keySet()) {
            if (stopConnection(connectionId)) {
                cleanupCount++;
            }
        }
        connectionManagers.clear();
        connectionStatus.clear();
        log.info("清理完成，共清理 {} 个连接", cleanupCount);
    }

    /**
     * 获取当前连接数
     */
    public int getConnectionCount() {
        return connectionManagers.size();
    }

    /**
     * 获取运行中的连接数
     */
    public int getRunningConnectionCount() {
        return (int) connectionStatus.values().stream().filter(Boolean::booleanValue).count();
    }
}