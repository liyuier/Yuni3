package com.yuier.yuni.adapter.onebot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.http.OneBotResponse;
import com.yuier.yuni.adapter.qq.websocket.module.WsRequest;
import com.yuier.yuni.core.bot.JsonCodec;
import com.yuier.yuni.core.model.event.OneBotEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Title: OneBotProtocolHandler
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot
 * @Date 2026/06/09
 * @description: OneBot 协议编解码层。
 *               负责 OneBot 事件的 JSON 反序列化、
 *               OneBot API 请求的构建与响应的解析。
 *               与具体传输方式（HTTP/WS）无关。
 */

public class OneBotProtocolHandler {

    private final ObjectMapper objectMapper;
    private final JsonCodec jsonCodec;

    public OneBotProtocolHandler(ObjectMapper objectMapper, JsonCodec jsonCodec) {
        this.objectMapper = objectMapper;
        this.jsonCodec = jsonCodec;
    }

    // ==================== 事件反序列化 ====================

    /**
     * 将 OneBot 上报的原始 JSON 反序列化为 OneBotEvent
     */
    public OneBotEvent deserializeEvent(String rawJson) {
        try {
            return objectMapper.readValue(rawJson, OneBotEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("OneBot 事件反序列化失败: " + rawJson, e);
        }
    }

    // ==================== API 请求构建 ====================

    /**
     * 构建 OneBot API WebSocket 请求
     */
    public WsRequest buildWsRequest(String action, Map<String, Object> params) {
        return new WsRequest(action, params != null ? new HashMap<>(params) : new HashMap<>());
    }

    public WsRequest buildWsRequest(String action) {
        return new WsRequest(action);
    }

    /**
     * 构建 OneBot API HTTP 请求参数
     */
    public Map<String, Object> buildHttpParams(String action, Map<String, Object> params) {
        return params != null ? new HashMap<>(params) : new HashMap<>();
    }

    /**
     * 获取对应的 OneBot API 端点路径
     */
    public String getApiEndpoint(String action) {
        return "/" + action;
    }

    // ==================== API 响应解析 ====================

    /**
     * 解析 HTTP 响应
     */
    public OneBotResponse parseHttpResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, OneBotResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("OneBot HTTP 响应解析失败", e);
        }
    }

    /**
     * 从 OneBotResponse 中提取 data 字段，反序列化为目标类型
     */
    public <T> T extractData(OneBotResponse response, Class<T> clazz) {
        if (response.getData() == null) {
            return null;
        }
        return objectMapper.convertValue(response.getData(), clazz);
    }

    // ==================== Echo 生成 ====================

    public String generateEcho() {
        return UUID.randomUUID().toString();
    }
}
