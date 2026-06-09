package com.yuier.yuni.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * @Title: OneBotDeserializer
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/22 5:58
 * @description: 通用 JSON 反序列化工具
 */

@Service
public class OneBotDeserializer {

    private final ObjectMapper objectMapper;

    public OneBotDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 泛型反序列化方法
     */
    public <T> T deserialize(String jsonString, Class<T> clazz) throws Exception {
        return objectMapper.readValue(jsonString, clazz);
    }
}
