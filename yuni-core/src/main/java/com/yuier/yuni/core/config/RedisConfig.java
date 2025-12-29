package com.yuier.yuni.core.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Title: RedisConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/28 18:37
 * @description:
 */

@Configuration
public class RedisConfig {

    /**
     * 自定义RedisTemplate Bean，配置JSON序列化规则
     * @param redisConnectionFactory  Redis连接工厂，由 Spring 自动注入
     * @return  配置好序列化规则的RedisTemplate<String, Object>实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 初始化 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 使 Jackson 可以访问所有字段进行序列化 / 反序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型信息，用于反序列化时正确识别对象类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        // 创建一个使用 Jackson 进行 JSON 序列化的 Redis 序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 创建一个用于字符串的 Redis 序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 RedisTemplate 的 key 序列化器为 StringRedisSerializer，确保 key 以字符串形式存储
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置 hash 类型的 key（field）也使用 StringRedisSerializer
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 RedisTemplate 的 value 序列化器为 Jackson2JsonRedisSerializer，使 value 以 JSON 格式存储
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置 hash 类型的 value 也使用 JSON 序列化器
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化 RedisTemplate 的属性，确保所有配置生效
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
