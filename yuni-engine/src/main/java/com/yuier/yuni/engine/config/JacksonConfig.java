package com.yuier.yuni.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.yuier.yuni.engine.manager.init.PolymorphicRegistrationProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @Title: JacksonConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/22 5:48
 * @description: 用于配置项目中 jackson 反序列化行为
 */

@Configuration
public class JacksonConfig {

    private final PolymorphicRegistrationProcessor registrationProcessor;

    public JacksonConfig(PolymorphicRegistrationProcessor registrationProcessor) {
        this.registrationProcessor = registrationProcessor;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 配置 snake_case 转换
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        // 应用多态类型注册
        registrationProcessor.applyTo(mapper);
        return mapper;
    }

    // 如果需要自定义反序列化器，也可以添加
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(ObjectMapper objectMapper) {
        return new Jackson2ObjectMapperBuilder();
    }
}
