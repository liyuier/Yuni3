package com.yuier.yuni.webapi.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Redis 键条目。
 */
@Data
@AllArgsConstructor
public class RedisKeyItem {
    /** 键名 */
    private String key;
    /** 数据类型 */
    private String type;
    /** 剩余过期时间（秒），-1 永不过期，-2 不存在 */
    private long ttl;
}
