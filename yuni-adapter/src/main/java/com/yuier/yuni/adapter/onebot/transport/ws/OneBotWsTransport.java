package com.yuier.yuni.adapter.onebot.transport.ws;

import com.yuier.yuni.adapter.config.OneBotProperties;
import com.yuier.yuni.adapter.onebot.transport.OneBotTransport;
import com.yuier.yuni.core.net.ws.yuni.ConnectionLostException;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.core.net.ws.yuni.YuniBusinessProxyListener;
import com.yuier.yuni.core.net.ws.yuni.WsRequestModel;
import com.yuier.yuni.core.bot.JsonCodec;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @Title: OneBotWsTransport
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot.transport.ws
 * @Date 2026/06/09
 * @description: OneBot WebSocket 传输层实现。
 *               管理两条 WS 连接：
 *               - /api：发送 API 请求、接收响应
 *               - /event：接收 OneBot 事件推送
 *               替代原 engine 模块中的 OneBotEventWsProxyListener
 *               和 adapter 中分散的 WS 连接管理逻辑。
 */

@Slf4j
public class OneBotWsTransport implements OneBotTransport {

    public static final String API_SOCKET_ID = "onebot_api";
    public static final String EVENT_SOCKET_ID = "onebot_event";

    private final OneBotProperties properties;
    private final JsonCodec jsonCodec;
    private final YuniWebSocketManager wsManager;

    private Consumer<String> eventCallback;

    /** /api 连接是否已建立 */
    private final AtomicBoolean apiConnected = new AtomicBoolean(false);

    /**
     * 重连信号锁。onOpen 时释放，sendApiRequest 在断联重试时等待它。
     * 初始 count=0 表示尚未需要等待（首次连接由 connect() 保证）。
     */
    private volatile CountDownLatch reconnectLatch = new CountDownLatch(0);

    /** /api 连接的 connector */
    private YuniWebSocketConnector apiConnector;

    public OneBotWsTransport(OneBotProperties properties,
                             JsonCodec jsonCodec,
                             YuniWebSocketManager wsManager) {
        this.properties = properties;
        this.jsonCodec = jsonCodec;
        this.wsManager = wsManager;
    }

    // ==================== 生命周期 ====================

    @Override
    public CompletableFuture<Void> connect() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            // 初始化重连锁（首次连接需等待 onOpen）
            reconnectLatch = new CountDownLatch(1);
            startApiSession();
            startEventSession();
            log.info("[OneBotWsTransport] WS 传输层已启动");
            future.complete(null);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            wsManager.closeConnection(API_SOCKET_ID);
            wsManager.closeConnection(EVENT_SOCKET_ID);
            log.info("[OneBotWsTransport] WS 传输层已断开");
            future.complete(null);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public String sendApiRequest(String action, Map<String, Object> params) {
        String requestJson = buildRequestJson(action, params);
        String echo = extractEcho(requestJson);
        try {
            return apiConnector.sendAndReceive(requestJson, echo);
        } catch (ConnectionLostException e) {
            log.warn("[OneBotWsTransport] 请求因断联失败，等待重连后重试: action={}", action);
            try {
                // 等待重连完成（超时时间从配置读取）
                int waitSeconds = properties.getWsReconnectWaitSeconds();
                if (reconnectLatch.await(waitSeconds, TimeUnit.SECONDS)) {
                    log.info("[OneBotWsTransport] 重连完成，重试请求: action={}", action);
                    // 重新构建请求（新 echo，避免与旧请求的 echo 冲突）
                    requestJson = buildRequestJson(action, params);
                    echo = extractEcho(requestJson);
                    return apiConnector.sendAndReceive(requestJson, echo);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("重连超时或中断，请求失败: action=" + action, e);
        }
    }

    @Override
    public void setEventCallback(Consumer<String> callback) {
        this.eventCallback = callback;
    }

    @Override
    public boolean isConnected() {
        return apiConnected.get();
    }

    // ==================== 连接管理 ====================

    private void startApiSession() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(properties.getWsUrl() + "/api")
                .addHeader("Authorization", "Bearer " + properties.getToken())
                .build();

        ApiWsListener listener = new ApiWsListener();
        apiConnector = new YuniWebSocketConnector(request, listener);
        apiConnector.setTimeOutInterval(properties.getWsTimeout());
        apiConnector.setHeartBeatInterval(properties.getWsHeartbeatInterval());
        wsManager.startNewConnection(API_SOCKET_ID, apiConnector);
    }

    private void startEventSession() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(properties.getWsUrl() + "/event")
                .addHeader("Authorization", "Bearer " + properties.getToken())
                .build();

        EventWsListener listener = new EventWsListener();
        YuniWebSocketConnector eventConnector = new YuniWebSocketConnector(request, listener);
        eventConnector.setTimeOutInterval(properties.getWsTimeout());
        eventConnector.setHeartBeatInterval(properties.getWsHeartbeatInterval());
        wsManager.startNewConnection(EVENT_SOCKET_ID, eventConnector);
    }

    // ==================== 请求构建 ====================

    private String buildRequestJson(String action, Map<String, Object> params) {
        Map<String, Object> request = new java.util.LinkedHashMap<>();
        request.put("action", action);
        request.put("params", params != null ? params : Map.of());
        request.put("echo", java.util.UUID.randomUUID().toString());
        return jsonCodec.toJson(request);
    }

    private String extractEcho(String requestJson) {
        try {
            Map<String, Object> map = jsonCodec.fromJson(requestJson, Map.class);
            return (String) map.get("echo");
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== WebSocket 监听器 ====================

    /**
     * /api 连接监听器：处理 API 响应，完成等待的 future。
     * 断联时立即失败所有 pending 请求，避免调用方空等超时。
     * 重连成功后释放信号锁，允许等待中的重试请求继续。
     */
    private class ApiWsListener implements YuniBusinessProxyListener {

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            log.info("[OneBotWsTransport] /api 连接建立成功");
            apiConnected.set(true);
            // 释放所有等待重连的 sendApiRequest 调用
            reconnectLatch.countDown();
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            try {
                Map<String, Object> responseMap = jsonCodec.fromJson(text, Map.class);
                String echo = (String) responseMap.get("echo");
                if (echo != null) {
                    WsRequestModel requestModel = apiConnector.getRequestModelMap().remove(echo);
                    if (requestModel != null) {
                        Integer retcode = (Integer) responseMap.get("retcode");
                        if (retcode != null && retcode == 0) {
                            // 返回完整响应 JSON，保持与 HTTP 传输层一致
                            // OneBotBot 统一通过 extractResponseData 解析
                            requestModel.getFuture().complete(text);
                        } else {
                            requestModel.getFuture().completeExceptionally(
                                    new RuntimeException("OneBot API 返回错误: retcode=" + retcode));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[OneBotWsTransport] /api 消息解析失败: {}", text, e);
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            log.info("[OneBotWsTransport] /api 收到二进制消息: {}", bytes);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            log.info("[OneBotWsTransport] /api 连接即将关闭: code={} reason={}", code, reason);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            log.info("[OneBotWsTransport] /api 连接已关闭");
            apiConnected.set(false);
            // 立即失败所有等待中的请求
            apiConnector.failAllPending("connection closed, code=" + code);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            log.error("[OneBotWsTransport] /api 连接错误: {}", t.getMessage());
            t.printStackTrace();
            apiConnected.set(false);
            // 立即失败所有等待中的请求，避免它们空等超时
            apiConnector.failAllPending("connection failure: " + t.getMessage());
            // 为下一轮重连准备新的锁
            reconnectLatch = new CountDownLatch(1);
            wsManager.restartConnection(API_SOCKET_ID);
        }
    }

    /**
     * /event 连接监听器：接收事件推送，转发给回调
     */
    private class EventWsListener implements YuniBusinessProxyListener {

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            log.info("[OneBotWsTransport] /event 连接建立成功");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            if (eventCallback != null) {
                try {
                    eventCallback.accept(text);
                } catch (Exception e) {
                    log.error("[OneBotWsTransport] 事件处理异常", e);
                }
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            log.info("[OneBotWsTransport] /event 收到二进制消息: {}", bytes);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            log.info("[OneBotWsTransport] /event 连接即将关闭: code={} reason={}", code, reason);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            log.info("[OneBotWsTransport] /event 连接已关闭");
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            log.error("[OneBotWsTransport] /event 连接错误: {}", t.getMessage());
            t.printStackTrace();
            wsManager.restartConnection(EVENT_SOCKET_ID);
        }
    }
}
