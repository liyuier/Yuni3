package com.yuier.yuni.plugin.model;

/**
 * @Title: PluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/23 22:05
 * @description: 插件实体类基类
 */

public interface PluginInstance {
    PluginMetadata getPluginMetadata();
    YuniPlugin getPlugin();
    void initialize() throws Exception;
    void destroy() throws Exception;
    String getJarFileName();
}

