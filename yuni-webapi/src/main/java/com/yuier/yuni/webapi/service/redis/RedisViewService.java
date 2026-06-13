package com.yuier.yuni.webapi.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.webapi.dto.redis.RedisKeyItem;
import com.yuier.yuni.webapi.dto.redis.RedisValueResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 查看服务。
 * 使用 StringRedisTemplate 直读原始字节，兼容非 Jackson 序列化的外部数据。
 */
@Service
@RequiredArgsConstructor
public class RedisViewService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<RedisKeyItem> searchKeys(String pattern) {
        String pat = (pattern == null || pattern.isBlank()) ? "*" : "*" + pattern + "*";
        Set<String> keys = stringRedisTemplate.keys(pat);
        if (keys == null || keys.isEmpty()) return List.of();

        return keys.stream()
                .sorted()
                .map(key -> {
                    String type = Optional.ofNullable(stringRedisTemplate.type(key))
                            .map(Object::toString).orElse("unknown");
                    Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
                    return new RedisKeyItem(key, type, ttl != null ? ttl : -2);
                })
                .collect(Collectors.toList());
    }

    public RedisValueResp getValue(String key) {
        var type = stringRedisTemplate.type(key);
        String typeName = type != null ? type.code() : "none";
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        String stringValue = null;
        Map<String, String> hashValue = null;

        try {
            if (type == null) {
                stringValue = "(key 不存在)";
            } else switch (type) {
                case STRING -> stringValue = stringRedisTemplate.opsForValue().get(key);
                case HASH -> {
                    Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
                    if (raw != null && !raw.isEmpty()) {
                        hashValue = new LinkedHashMap<>();
                        for (Map.Entry<Object, Object> e : raw.entrySet()) {
                            String val = e.getValue() != null
                                    ? tryPrettyJson(String.valueOf(e.getValue())) : "";
                            hashValue.put(String.valueOf(e.getKey()), val);
                        }
                    }
                }
                case NONE -> stringValue = "(key 不存在)";
                default -> {
                    Object val = stringRedisTemplate.opsForValue().get(key);
                    stringValue = val != null ? String.valueOf(val) : "(无法读取)";
                }
            }
        } catch (Exception e) {
            stringValue = "(读取失败: " + e.getMessage() + ")";
        }

        // 对 string 类型值尝试 JSON 美化
        if (stringValue != null && !"(key 不存在)".equals(stringValue)) {
            stringValue = tryPrettyJson(stringValue);
        }
        // 对 hash 字段值也尝试美化
        if (hashValue != null) {
            for (Map.Entry<String, String> e : hashValue.entrySet()) {
                e.setValue(tryPrettyJson(e.getValue()));
            }
        }

        return new RedisValueResp(typeName, stringValue, hashValue, ttl != null ? ttl : -2);
    }

    /**
     * 如果字符串是合法 JSON，返回 pretty-print 版本；否则原样返回。
     */
    private String tryPrettyJson(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) return raw;
        try {
            Object parsed = objectMapper.readValue(raw, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
        } catch (Exception e) {
            return raw;
        }
    }
}
