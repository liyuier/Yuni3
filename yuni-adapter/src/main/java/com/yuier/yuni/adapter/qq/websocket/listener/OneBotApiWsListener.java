package com.yuier.yuni.adapter.qq.websocket.listener;

import com.yuier.yuni.adapter.config.OneBotCommunicate;
import com.yuier.yuni.adapter.qq.websocket.module.WsResponse;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketListener;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @Title: OneBotApiWsListener
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket
 * @Date 2025/12/29 3:30
 * @description:
 */

@Data
@Slf4j
@Component
public class OneBotApiWsListener extends YuniWebSocketListener {

    @Autowired
    OneBotCommunicate config;
    @Autowired
    OneBotDeserializer deserializer;
    @Autowired
    OneBotSerialization serialization;

    // 持有一下自己所在的 connector
    private YuniWebSocketConnector connector;

    /**
     * WebSocket 连接完全关闭
     * @param webSocket  已关闭的 WebSocket 连接对象。
     * @param code  关闭状态码
     * @param reason  关闭原因的描述字符串
     */
    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    /**
     * WebSocket 连接即将关闭（即收到关闭帧时）
     * @param webSocket  当前的 WebSocket 连接对象
     * @param code  关闭状态码，表示关闭的原因
     * @param reason  关闭原因的描述字符串
     */
    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    /**
     * WebSocket 连接过程中发生错误（如网络异常、握手失败等）
     * @param webSocket  发生错误时的 WebSocket 连接对象（可能为 null，如果连接未成功建立）
     * @param t  发生的异常对象
     * @param response  如果错误发生在握手阶段，可能包含服务器的响应信息；否则可能为 null
     */
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.info("到 {} 的连接建立失败。",  config.getWsUrl() + "/api");
    }

    /**
     * 从服务器接收到文本消息
     * @param webSocket  当前的 WebSocket 连接对象
     * @param text  接收到的文本消息内容
     */
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        // 在 /api 端点上，只会收到响应类消息
        try {
            WsResponse response = deserializer.deserialize(text, WsResponse.class);
            // 根据 echo 取出 future
            CompletableFuture<String> future = connector.getRequestFutures().remove(response.getEcho());
            if (future != null) {
                // 检查状态码
                if (response.getRetcode() == 0) {
                    // 状态码正常，future 返回响应数据
                    future.complete(serialization.serialize(response.getData()));
                } else {
                    // 状态码异常，future 返回异常
                    future.completeExceptionally(new RuntimeException("请求失败，状态码 " + response.getRetcode()));
                }
            } else {
                log.warn("接收到非请求消息: {}", text);
            }
        } catch (Exception e) {
            log.warn("接收到非请求消息: {}", text);
        }
    }

    /**
     * 从服务器接收到二进制消息
     * @param webSocket  当前的 WebSocket 连接对象
     * @param bytes  接收到的二进制消息内容
     */
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    /**
     * WebSocket 连接成功
     * @param webSocket  已经建立的 WebSocket 连接对象，可以通过该对象发送消息或关闭连接
     * @param response  服务器返回的 HTTP 响应，通常包含握手成功的信息
     */
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.info("到 {} 的连接建立成功。",  config.getWsUrl() + "/api");
    }
}
