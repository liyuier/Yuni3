package com.yuier.yuni.core.net.ws.yuni;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Title: YuniWebSocketListener
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2025/12/29 2:53
 * @description:
 */

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class YuniWebSocketListener extends WebSocketListener {

    private YuniBusinessProxyListener proxyListener;

    /**
     * WebSocket 连接完全关闭
     * @param webSocket  已关闭的 WebSocket 连接对象。
     * @param code  关闭状态码
     * @param reason  关闭原因的描述字符串
     */
    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("[YuniWebSocketListener.onClosed]连接已关闭，状态码: {}, 原因: {}", code, reason);
        proxyListener.onClosed(webSocket, code, reason);
    }

    /**
     * WebSocket 连接即将关闭（即收到关闭帧时）
     * @param webSocket  当前的 WebSocket 连接对象
     * @param code  关闭状态码，表示关闭的原因
     * @param reason  关闭原因的描述字符串
     */
    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.info("[YuniWebSocketListener.onClosing]连接即将关闭，状态码: {}, 原因: {}", code, reason);
        proxyListener.onClosing(webSocket, code, reason);
    }

    /**
     * WebSocket 连接过程中发生错误（如网络异常、握手失败等）
     * @param webSocket  发生错误时的 WebSocket 连接对象（可能为 null，如果连接未成功建立）
     * @param t  发生的异常对象
     * @param response  如果错误发生在握手阶段，可能包含服务器的响应信息；否则可能为 null
     */
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.info("[YuniWebSocketListener.onFailure]连接发生错误，服务器响应: {}, 错误堆栈: ", response);
        t.printStackTrace();
        proxyListener.onFailure(webSocket, t, response);
    }

    /**
     * 从服务器接收到文本消息
     * @param webSocket  当前的 WebSocket 连接对象
     * @param text  接收到的文本消息内容
     */
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        log.info("[YuniWebSocketListener.onMessage]收到文本消息: {}", text);
        proxyListener.onMessage(webSocket, text);
    }

    /**
     * 从服务器接收到二进制消息
     * @param webSocket  当前的 WebSocket 连接对象
     * @param bytes  接收到的二进制消息内容
     */
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        log.info("[YuniWebSocketListener.onMessage]收到二进制消息: {}", bytes);
        proxyListener.onMessage(webSocket, bytes);
    }

    /**
     * WebSocket 连接成功
     * @param webSocket  已经建立的 WebSocket 连接对象，可以通过该对象发送消息或关闭连接
     * @param response  服务器返回的 HTTP 响应，通常包含握手成功的信息
     */
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.info("[YuniWebSocketListener.onOpen]连接成功，服务器响应: {}", response);
        proxyListener.onOpen(webSocket, response);
    }
}
