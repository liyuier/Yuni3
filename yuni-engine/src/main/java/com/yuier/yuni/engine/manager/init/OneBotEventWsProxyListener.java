package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.adapter.config.OneBotCommunicate;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.net.ws.yuni.YuniBusinessProxyListener;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.engine.event.EventBridge;
import com.yuier.yuni.event.detector.message.command.CommandMatcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.yuier.yuni.adapter.qq.websocket.OneBotSessionIdConstance.ONEBOT_EVENT_SOCKET_ID;

/**
 * @Title: OneBotEventWsProxyListener
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/31 8:07
 * @description: 事件监听器代理
 */

@Slf4j
@Data
public class OneBotEventWsProxyListener implements YuniBusinessProxyListener {

    private OneBotAdapter adapter;
    private EventBridge eventBridge;
    OneBotCommunicate config;
    YuniWebSocketManager manager;

    public OneBotEventWsProxyListener(OneBotAdapter adapter, EventBridge eventBridge, OneBotCommunicate config, YuniWebSocketManager manager) {
        this.adapter = adapter;
        this.eventBridge = eventBridge;
        this.config = config;
        this.manager = manager;
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("到 {} 的连接已经关闭。",  config.getWsUrl() + "/event");
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("到 {} 的连接即将关闭。",  config.getWsUrl() + "/event");
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.info("到 {} 的连接发生错误。错误信息：{}, 响应信息：{} 。 即将重启连接。",  config.getWsUrl() + "/event", t.getMessage(), response);
        t.printStackTrace();
        manager.restartConnection(ONEBOT_EVENT_SOCKET_ID);
        log.info("已重启到 {} 的连接。",  config.getWsUrl() + "/event");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        try {
            handleTextMessage(text);
        } catch (Exception e) {
            log.error("处理 OneBot 消息时发生错误，即将重启。");
            e.printStackTrace();
            manager.restartConnection(ONEBOT_EVENT_SOCKET_ID);
            log.info("已重启到 {} 的连接。",  config.getWsUrl() + "/event");
        } finally {
            clearSystemAfterHandleMessage();
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        log.info("收到 OneBot 发来二进制的消息: {}", bytes);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.info("到 {} 的连接建立成功。",  config.getWsUrl() + "/event");
    }

    private void handleTextMessage(String text) {
        OneBotEvent oneBotEvent = adapter.handleReportJson(text);
        oneBotEvent.setRawJson(text);
        eventBridge.publishRawEvent(oneBotEvent);
    }

    /**
     * 处理完消息后，重置一下系统状态
     */
    private void clearSystemAfterHandleMessage() {
        CommandMatcher.clear();
    }
}
