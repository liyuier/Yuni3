package com.yuier.yuni.webapi.websocket;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.engine.event.message.ChatSession;
import com.yuier.yuni.engine.manager.context.RequestContextContainer;
import com.yuier.yuni.engine.manager.context.RequestContextManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @Title: YuniWebSocketHandler
 * @Author yuier
 * @Package com.yuier.yuni.webapi.websocket
 * @Date 2025/12/22 19:14
 * @description: ws 管理器
 */

@Component
@Slf4j
public class YuniWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private OneBotAdapter adapter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接建立，Session ID: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {
            // 为当前WebSocket消息创建业务上下文容器
            RequestContextContainer requestContextContainer = new RequestContextContainer();

            // 创建 chatSession 对象并放入容器中
            requestContextContainer.setChatSession(new ChatSession(adapter));

            // 将容器放入 ThreadLocal
            RequestContextManager.setContext(requestContextContainer);

            // 处理消息逻辑可以在这里添加
            log.info("处理WebSocket消息: {}", message.getPayload());

        } finally {
            // 消息处理结束后清理上下文，防止内存泄露
            RequestContextManager.clear();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket连接关闭，Session ID: {}, Status: {}", session.getId(), status);
    }
}
