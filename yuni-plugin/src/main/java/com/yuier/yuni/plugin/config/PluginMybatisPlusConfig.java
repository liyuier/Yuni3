package com.yuier.yuni.plugin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;

/**
 * @Title: PluginMybatisPlusConfig
 * @Author yuier
 * @Package com.yuier.yuni.plugin.config
 * @Date 2025/12/24 21:30
 * @description:
 */

@SpringBootConfiguration
@MapperScan("com.yuier.yuni.plugin.mapper")
public class PluginMybatisPlusConfig {

}
