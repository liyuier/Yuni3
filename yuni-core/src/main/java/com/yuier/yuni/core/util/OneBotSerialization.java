package com.yuier.yuni.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * @Title: OneBotSerialization
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/23 3:46
 * @description: 序列化工具
 */

@Service
public class OneBotSerialization {

    private final ObjectMapper objectMapper;

    public OneBotSerialization(ObjectMapper objectMapper) {  // 依赖注入 ObjectMapper
        this.objectMapper = objectMapper;
    }

    public String serialize(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

}
