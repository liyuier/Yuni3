package com.yuier.yuni.webapi.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Redis 值响应。
 */
@Data
@AllArgsConstructor
public class RedisValueResp {
    /** 数据类型 */
    private String type;
    /** String 类型的值 */
    private String stringValue;
    /** Hash 类型的值（已格式化） */
    private Map<String, String> hashValue;
    /** 剩余 TTL 秒 */
    private long ttl;
}
