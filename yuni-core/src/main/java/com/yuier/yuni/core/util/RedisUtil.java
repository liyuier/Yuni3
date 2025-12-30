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

    /* String类型静态方法 */

    /**
     * 设置 Redis 中的 String 类型数据，key 为字符串，value 为任意对象
     * @param key  key
     * @param value  value
     */
    public static void set(String key, Object value) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置缓存失败：" + e.getMessage());
        }
    }

    /**
     * 带过期时间的设置 Redis 中的 String 类型数据，key 为字符串，value 为任意对象
     * @param key  key
     * @param value  value
     * @param timeout  过期时间
     * @param timeUnit  时间单位
     */
    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            valueOperations.set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置缓存（带过期时间）失败：" + e.getMessage());
        }
    }

    /**
     * 获取 Redis 中的 String 类型数据，key 为字符串
     * @param key  key
     * @return  value
     */
    public static Object get(String key) {
        try {
            ValueOperations<String, Object> valueOperations = staticRedisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis获取缓存失败：" + e.getMessage());
        }
    }

    /* Hash类型静态方法 */

    /**
     * 设置 Redis 中的 Hash 类型数据，key 为字符串，hashKey 为字符串，value 为任意对象
     * @param key  key
     * @param hashKey  hashKey
     * @param value  value
     */
    public static void hSet(String key, String hashKey, Object value) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            hashOperations.put(hashKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash设置值失败：" + e.getMessage());
        }
    }

    /**
     * 获取 Redis 中的 Hash 类型数据的 value，key 为字符串，hashKey 为字符串
     * @param key  key
     * @param hashKey  hashKey
     * @return  value
     */
    public static Object hGet(String key, String hashKey) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            return hashOperations.get(hashKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash获取值失败：" + e.getMessage());
        }
    }

    /**
     * 获取 Redis 中的 Hash 类型数据所有的 key 和 value，key 为字符串
     * @param key  key
     * @return  value
     */
    public static Map<String, Object> hGetAll(String key) {
        try {
            BoundHashOperations<String, String, Object> hashOperations = staticRedisTemplate.boundHashOps(key);
            return hashOperations.entries();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis Hash获取所有值失败：" + e.getMessage());
        }
    }

    /* 通用静态方法 */

    /**
     * 删除 Redis 中的一个或多个 key
     * @param key  key
     */
    public static void delete(String... key) {
        try {
            staticRedisTemplate.delete(List.of(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis删除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除 Redis 中的多个 key
     * @param keys  key
     */
    public static void delete(Collection<String> keys) {
        try {
            staticRedisTemplate.delete(keys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis批量删除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 判断 Redis 中是否存在某个 key
     * @param key  key
     * @return  是否存在
     */
    public static boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(staticRedisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis判断key是否存在失败：" + e.getMessage());
        }
    }

    /**
     * 设置 Redis 中某个 key 的过期时间
     * @param key  key
     * @param timeout  过期时间
     * @param timeUnit  时间单位
     * @return  是否成功
     */
    public static boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(staticRedisTemplate.expire(key, timeout, timeUnit));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Redis设置key过期时间失败：" + e.getMessage());
        }
    }
}
