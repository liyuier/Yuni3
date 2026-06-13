package com.yuier.yuni.webapi.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 聊天 WebSocket 处理器。
 * 维护会话 → 群组订阅关系，支持推送消息到指定群组的订阅者。
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    /** sessionId → 已订阅的群组 ID 集合 */
    private final Map<String, Set<Long>> subscriptions = new ConcurrentHashMap<>();
    /** groupId → 订阅该群的 session 集合 */
    private final Map<Long, Set<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        subscriptions.put(session.getId(), new CopyOnWriteArraySet<>());
        log.debug("WebSocket 连接: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String action = (String) payload.get("action");
        Object groupIdObj = payload.get("group_id");
        Long groupId = groupIdObj instanceof Number ? ((Number) groupIdObj).longValue() : null;

        if ("subscribe".equals(action) && groupId != null) {
            subscriptions.get(session.getId()).add(groupId);
            groupSessions.computeIfAbsent(groupId, k -> new CopyOnWriteArraySet<>()).add(session);
        } else if ("unsubscribe".equals(action) && groupId != null) {
            Set<Long> subs = subscriptions.get(session.getId());
            if (subs != null) subs.remove(groupId);
            Set<WebSocketSession> sessions = groupSessions.get(groupId);
            if (sessions != null) sessions.remove(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Set<Long> subs = subscriptions.remove(session.getId());
        if (subs != null) {
            for (Long groupId : subs) {
                Set<WebSocketSession> sessions = groupSessions.get(groupId);
                if (sessions != null) sessions.remove(session);
            }
        }
        log.info("WebSocket 断开: {}", session.getId());
    }

    /**
     * 推送消息到指定群组的所有订阅者。
     * @param groupId 群号
     * @param json    消息 JSON
     */
    public void pushToGroup(Long groupId, String json) {
        Set<WebSocketSession> sessions = groupSessions.get(groupId);
        if (sessions == null || sessions.isEmpty()) return;
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(msg);
                }
            } catch (Exception e) {
                log.debug("推送消息到 {} 失败: {}", session.getId(), e.getMessage());
            }
        }
    }
}
