package com.yuier.yuni.webapi.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.event.ws.YuniMessagePushEvent;
import com.yuier.yuni.webapi.dto.group.GroupMessageItem;
import com.yuier.yuni.webapi.dto.ws.WsMessage;
import com.yuier.yuni.webapi.service.group.ImageRkeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * WebSocket 消息推送服务。
 * 监听 {@link YuniMessagePushEvent}，将实时消息推送到前端 WebSocket 客户端。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WsMessagePushService {

    private final ChatWebSocketHandler chatHandler;
    private final ObjectMapper objectMapper;
    private final ImageRkeyService imageRkeyService;

    @EventListener
    public void onMessagePush(YuniMessagePushEvent event) {
        try {
            List<Map<String, Object>> segments = parseSegments(event.getRawJson());
            imageRkeyService.refreshSegments(segments);
            GroupMessageItem item = new GroupMessageItem(
                    event.getSenderName(),
                    event.getRawMessage() != null ? event.getRawMessage() : "",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    event.isSelfSent(),
                    event.isPlainText(),
                    segments
            );

            WsMessage<GroupMessageItem> wsMsg = new WsMessage<>("new_message", item);
            String json = objectMapper.writeValueAsString(wsMsg);
            chatHandler.pushToGroup(event.getGroupId(), json);
        } catch (Exception e) {
            log.debug("WebSocket 推送失败: {}", e.getMessage());
        }
    }

    private List<Map<String, Object>> parseSegments(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) return List.of();
        try {
            return objectMapper.readValue(rawJson, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
