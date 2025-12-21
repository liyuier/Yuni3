package com.yuier.yuni.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.core.model.event.OneBotEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @Title: OneBotDeserializer
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/22 5:58
 * @description: OneBot 上报 json 反序列化工具
 */

@Service
public class OneBotDeserializer {

    private final ObjectMapper objectMapper;

    public OneBotDeserializer(ObjectMapper objectMapper) {  // 依赖注入 ObjectMapper
        this.objectMapper = objectMapper;
    }

    /**
     * 手动反序列化 OneBot 事件
     */
    public OneBotEvent deserializeEvent(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, OneBotEvent.class);
    }

    /**
     * 泛型反序列化方法
     */
    public <T> T deserialize(String jsonString, Class<T> clazz) throws Exception {
        return objectMapper.readValue(jsonString, clazz);
    }

    /**
     * 异步反序列化（如果需要）
     */
    public CompletableFuture<OneBotEvent> deserializeEventAsync(String jsonString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return deserializeEvent(jsonString);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
