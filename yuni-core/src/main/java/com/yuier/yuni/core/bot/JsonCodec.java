package com.yuier.yuni.core.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Title: JsonCodec
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 通用 JSON 编解码工具。
 *               封装 ObjectMapper，提供序列化与反序列化方法。
 *               替代原先 OneBotDeserializer / OneBotSerialization 的通用功能。
 */

@Slf4j
@Component
public class JsonCodec {

    private final ObjectMapper objectMapper;

    public JsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 将对象序列化为 JSON 字符串
     * @param obj 待序列化对象
     * @return JSON 字符串
     * @throws RuntimeException 序列化失败时抛出
     */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 序列化失败", e);
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @return 反序列化后的对象
     * @throws RuntimeException 反序列化失败时抛出
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("JSON 反序列化失败, target class: {}", clazz.getName(), e);
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * 将对象转换为另一个类型的对象（先序列化再反序列化）
     * @param source 源对象
     * @param targetClass 目标类型
     * @return 转换后的对象
     */
    public <T> T convertValue(Object source, Class<T> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }

    /**
     * 获取底层 ObjectMapper（供需要直接使用 ObjectMapper 的场景）
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
