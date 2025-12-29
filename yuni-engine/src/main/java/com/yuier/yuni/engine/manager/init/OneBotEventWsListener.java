package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.adapter.config.OneBotCommunicate;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketListener;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.engine.event.EventBridge;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title: OneBotEventWsListener
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket
 * @Date 2025/12/29 3:31
 * @description:
 */

@Slf4j
@Component
public class OneBotEventWsListener extends YuniWebSocketListener {

    @Autowired
    private OneBotAdapter adapter;
    @Autowired
    private EventBridge eventBridge;

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        log.info("收到 OneBot 发来的消息: {}", text);
        OneBotEvent oneBotEvent = adapter.handleReportJson(text);
        oneBotEvent.setRawJson(text);
        eventBridge.publishRawEvent(oneBotEvent);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        OneBotCommunicate config = SpringContextUtil.getBean(OneBotCommunicate.class);
        log.info("到 {} 的连接建立成功。",  config.getWsUrl() + "/event");
    }
}
