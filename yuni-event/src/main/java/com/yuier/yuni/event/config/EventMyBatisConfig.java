package com.yuier.yuni.event.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;

/**
 * @Title: EventMyBatisConfig
 * @Author yuier
 * @Package com.yuier.yuni.event.config
 * @Date 2025/12/27 1:45
 * @description:
 */

@SpringBootConfiguration
@MapperScan("com.yuier.yuni.event.mapper")
public class EventMyBatisConfig {
}
