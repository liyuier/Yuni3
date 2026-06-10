package com.yuier.yuni.core.net.ws.yuni;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Title: YuniWebSocketConnector
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2025/12/29 3:22
 * @description:  OkHttp WebSocket 连接包装类，用于启动连接、通过 webSocket 维护连接等
 */

@Data
@NoArgsConstructor
@Slf4j
public class YuniWebSocketConnector {

    private OkHttpClient client;
    private Request request;
    private YuniWebSocketListener listener;

    private WebSocket webSocket;

    private Map<String, WsRequestModel> requestModelMap = new ConcurrentHashMap<>();

    private int timeOutInterval = 3000;  // 默认超时时间为 3 秒
    private int heartBeatInterval = 30000;  // 默认心跳间隔为 30 秒

    /** 连接健康标志，由 YuniWebSocketListener 在 onOpen/onClosed/onFailure 中更新 */
    private final AtomicBoolean connectionHealthy = new AtomicBoolean(false);

    public YuniWebSocketConnector(Request request, YuniBusinessProxyListener proxyListener) {
        this.client = new OkHttpClient();
        this.request = request;
        this.listener = new YuniWebSocketListener(proxyListener, this);
    }

    /**
     * 由 YuniWebSocketListener 在 onOpen 回调，标记连接健康。
     */
    public void onConnectionOpened() {
        connectionHealthy.set(true);
    }

    /**
     * 由 YuniWebSocketListener 在 onClosed / onFailure 回调，标记连接不健康。
     */
    public void onConnectionFailed() {
        connectionHealthy.set(false);
    }

    public WebSocket startConnection() {
        // 启动并设置 WebSocket
        webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }

    public WebSocket restartConnection() {
        // 先失败所有等待中的请求，避免它们无限等待超时
        failAllPending("connection restarting");
        // 重新启动 WebSocket
        webSocket.close(1000, "重新连接");
        webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }

    /**
     * 立即失败所有等待响应的请求。
     * 在连接断开或重启时调用，避免调用方空等超时。
     * @param reason 失败原因描述
     */
    public void failAllPending(String reason) {
        requestModelMap.forEach((echo, model) -> {
            if (!model.getFuture().isDone()) {
                model.getFuture().completeExceptionally(
                        new ConnectionLostException("WebSocket 连接丢失: " + reason + ", echo=" + echo));
            }
        });
        requestModelMap.clear();
    }

    public void send(String message) {
        // 发送消息
        webSocket.send(message);
    }

    public String sendAndReceive(String message, String echoFlag) {
        // 发送消息，并创建回调 future
        CompletableFuture<String> future = new CompletableFuture<>();
        // 将发送出去的消息加入等待队列
        WsRequestModel wsRequestModel = new WsRequestModel(future, echoFlag, message);
        requestModelMap.put(echoFlag, wsRequestModel);
        webSocket.send(message);

        // 设置超时时间
        setTimeOut(wsRequestModel, echoFlag);

        // 等待 future 完成
        try {
            return future.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            log.debug("[YuniWebSocketConnector.sendAndReceive]请求失败，失败消息: {}", cause.getMessage());
            // ConnectionLostException 向上传播，让上层有机会重试
            if (cause instanceof ConnectionLostException) {
                throw (ConnectionLostException) cause;
            }
            // 二次检查连接健康：覆盖 onFailure 在超时和此 catch 之间的窄窗口
            if (!connectionHealthy.get()) {
                throw new ConnectionLostException("连接已断开: " + cause.getMessage());
            }
            return "";
        }
    }

    private void setTimeOut(WsRequestModel wsRequestModel, String echoFlag) {
        new Thread(() -> {
            try {
                Thread.sleep(timeOutInterval); // 循环等待直到心跳间隔结束
                if (wsRequestModel.getFuture().isDone()){  // 这玩意会在 listener 里处理
                    return;
                }
                // 连接不健康时抛 ConnectionLostException，触发上层重连重试；
                // 连接健康时抛普通 RuntimeException，由调用方自行处理
                if (!connectionHealthy.get()) {
                    wsRequestModel.getFuture().completeExceptionally(
                            new ConnectionLostException("连接已断开，请求超时: " + wsRequestModel.getMessage()));
                } else {
                    wsRequestModel.getFuture().completeExceptionally(
                            new RuntimeException("一条请求超时，请求内容为: " + wsRequestModel.getMessage()));
                }
                requestModelMap.remove(echoFlag);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}
