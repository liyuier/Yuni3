package com.yuier.yuni.adapter.onebot.transport.http;

import com.yuier.yuni.adapter.config.OneBotProperties;
import com.yuier.yuni.adapter.onebot.transport.OneBotTransport;
import com.yuier.yuni.core.bot.JsonCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @Title: OneBotHttpTransport
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot.transport.http
 * @Date 2026/06/09
 * @description: OneBot HTTP 传输层实现。
 *               通过 HTTP POST 接收事件推送，
 *               通过 HTTP POST 向 OneBot API 发送请求。
 */

@Slf4j
public class OneBotHttpTransport implements OneBotTransport {

    private final OneBotProperties properties;
    private final RestTemplate restTemplate;
    private final JsonCodec jsonCodec;

    /** 事件回调（由 OneBotBot 注入） */
    private Consumer<String> eventCallback;

    public OneBotHttpTransport(OneBotProperties properties, JsonCodec jsonCodec) {
        this.properties = properties;
        this.jsonCodec = jsonCodec;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<Void> connect() {
        log.info("[OneBotHttpTransport] HTTP 传输层就绪，等待 OneBot 事件推送");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        log.info("[OneBotHttpTransport] HTTP 传输层已断开");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String sendApiRequest(String action, Map<String, Object> params) {
        String url = properties.getHttpUrl() + "/" + action;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                params != null ? params : new HashMap<>(), headers);

        log.debug("[OneBotHttpTransport] POST {} params={}", url, params);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        // 返回完整的 OneBot 响应 JSON，由 OneBotBot 统一解析
        return response.getBody();
    }

    @Override
    public void setEventCallback(Consumer<String> callback) {
        this.eventCallback = callback;
    }

    @Override
    public boolean isConnected() {
        return true; // HTTP 无连接，始终可用
    }

    /**
     * 接收 OneBot HTTP POST 推送的事件 JSON
     * （由 Controller 调用）
     */
    public void receiveEvent(String rawJson) {
        if (eventCallback != null) {
            eventCallback.accept(rawJson);
        } else {
            log.warn("[OneBotHttpTransport] 收到事件推送但未注册回调，丢弃");
        }
    }
}
