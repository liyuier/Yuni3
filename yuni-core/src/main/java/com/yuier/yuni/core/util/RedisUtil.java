package com.yuier.yuni.core.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title: RedisUtil
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/28 18:39
 * @description:
 */

@Component
public class RedisUtil {

    private static RedisTemplate<String, Object> staticRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        staticRedisTemplate = this.redisTemplate;
    }

    // ====================== String类型静态方法 ======================
    public static void set(String key, Object value) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置缓存失败：" + e.getMessage());
        }
    }

    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            valueOperations.set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置缓存（带过期时间）失败：" + e.getMessage());
        }
    }

    public static Object get(String key) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis获取缓存失败：" + e.getMessage());
        }
    }

    // ====================== Hash类型静态方法 ======================
    public static void hSet(String key, String hashKey, Object value) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            hashOperations.put(hashKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash设置值失败：" + e.getMessage());
        }
    }

    public static Object hGet(String key, String hashKey) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            return hashOperations.get(hashKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash获取值失败：" + e.getMessage());
        }
    }

    public static Map<String, Object> hGetAll(String key) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            return hashOperations.entries();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash获取所有值失败：" + e.getMessage());
        }
    }

    // ====================== 通用静态方法 ======================
    public static void delete(String... key) {
        try {
            staticRedisTemplate.delete(List.of(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis删除缓存失败：" + e.getMessage());
        }
    }

    public static void delete(Collection<String> keys) {
        try {
            staticRedisTemplate.delete(keys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis批量删除缓存失败：" + e.getMessage());
        }
    }

    public static boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(staticRedisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis判断key是否存在失败：" + e.getMessage());
        }
    }

    public static boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(staticRedisTemplate.expire(key, timeout, timeUnit));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置key过期时间失败：" + e.getMessage());
        }
    }
}
