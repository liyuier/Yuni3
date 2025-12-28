package com.yuier.yuni.core.net.ws.example;

import com.yuier.yuni.core.net.ws.CommonWebSocketManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Title: WebSocketUsageExample
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.example
 * @Date 2025/12/25 15:22
 * @description: 使用示例
 */

@Component
public class WebSocketUsageExample {

    private final CommonWebSocketManager webSocketManager;

    public WebSocketUsageExample(CommonWebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;

        // 模拟运行时动态创建和管理连接
//        simulateRuntimeOperations();
    }

    private void simulateRuntimeOperations() {
        // 延迟执行演示，模拟运行时操作
        Executors.newSingleThreadScheduledExecutor()
                .schedule(this::performDynamicOperations, 2, TimeUnit.SECONDS);
    }

    private void performDynamicOperations() {
//        System.out.println("\n=== 开始动态WebSocket连接操作演示 ===");
//
//        // 1. 创建第一个连接 - 使用自定义业务处理器
//        String connection1Id = "server-connection-1";
//        String serverUrl1 = "ws://localhost:8080/websocket1";
//        webSocketManager.createConnection(connection1Id, serverUrl1,
//                new CommonWebSocketHandler(connection1Id, serverUrl1, webSocketManager, new DefaultBusinessMessageProxyHandler()), "");
//
//        // 2. 启动第一个连接
//        webSocketManager.startConnection(connection1Id);
//
//        // 3. 检查连接状态
//        System.out.println("连接1状态: " + webSocketManager.isConnectionRunning(connection1Id));
//
//        // 4. 创建第二个连接 - 使用默认业务处理器
//        String connection2Id = "server-connection-2";
//        String serverUrl2 = "ws://localhost:8080/websocket2";
//        webSocketManager.createConnection(connection2Id, serverUrl2,
//                new CommonWebSocketHandler(connection2Id, serverUrl2, webSocketManager, new DefaultBusinessMessageProxyHandler()), "");
//
//        // 5. 启动第二个连接
//        webSocketManager.startConnection(connection2Id);
//
//        // 6. 列出所有连接
//        System.out.println("所有连接: " + webSocketManager.getAllConnections());
//
//        // 7. 停止第一个连接
//        webSocketManager.stopConnection(connection1Id);
//
//        // 8. 检查连接状态
//        System.out.println("连接1状态: " + webSocketManager.isConnectionRunning(connection1Id));
//        System.out.println("连接2状态: " + webSocketManager.isConnectionRunning(connection2Id));
//
//        System.out.println("=== 动态WebSocket连接操作演示完成 ===\n");
    }
}
