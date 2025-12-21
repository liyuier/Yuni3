package com.yuier.yuni.core.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Title: YuniAutoConfiguration
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/22 1:53
 * @description: 核心默认配置类
 */

@SpringBootConfiguration
@ComponentScan(basePackages = "com.yuier.yuni")
public class YuniAutoConfiguration {
}
