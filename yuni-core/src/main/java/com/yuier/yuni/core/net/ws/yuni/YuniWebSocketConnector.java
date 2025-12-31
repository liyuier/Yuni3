package com.yuier.yuni.core.net.ws.yuni;

import lombok.AllArgsConstructor;
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

    private Map<String, CompletableFuture<String>> requestFutures = new ConcurrentHashMap<>();

    private int timeOutInterval = 3000;  // 默认超时时间为 3 秒
    private int heartBeatInterval = 30000;  // 默认心跳间隔为 30 秒

    public YuniWebSocketConnector(Request request, YuniBusinessProxyListener proxyListener) {
        this.client = new OkHttpClient();
        this.request = request;
        this.listener = new YuniWebSocketListener(proxyListener);
    }

    public WebSocket startConnection() {
        // 启动并设置 WebSocket
        webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }

    public WebSocket restartConnection() {
        // 重新启动 WebSocket
        webSocket.close(1000, "重新连接");
        webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }

    public void send(String message) {
        // 发送消息
        webSocket.send(message);
    }

    public String sendAndReceive(String message, String echoFlag) {
        // 发送消息，并创建回调 future
        CompletableFuture<String> future = new CompletableFuture<>();
        // 将发送出去的消息加入等待队列
        requestFutures.put(echoFlag, future);
        webSocket.send(message);

        // 设置超时时间
        setTimeOut(future, echoFlag);

        // 等待 future 完成
        try {
            return future.join();
        } catch (CompletionException e) {
            log.info("[YuniWebSocketConnector.sendAndReceive]请求失败，失败消息: {}", e.getCause().getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private void setTimeOut(CompletableFuture<String> future, String echoFlag) {
        new Thread(() -> {  // 启动一个新线程等待到配置的超时时间结束
            try {
                Thread.sleep(timeOutInterval); // 等待直到超时结束
                if (future.isDone()){  // 这玩意会在 listener 里处理
                    return;
                }
                // 如果到超时时间发现 future 没有完成，则认为请求超时，抛出异常，移除该 future
                future.completeExceptionally(new RuntimeException("一条请求超时，消息标识为: " + echoFlag));
                requestFutures.remove(echoFlag);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
