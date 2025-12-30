package com.yuier.yuni.plugin.model;

import lombok.Data;

/**
 * @Title: PluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/23 22:05
 * @description: 插件实体类基类
 */

@Data
public abstract class PluginInstance {

    private YuniPlugin plugin;
    private PluginMetadata pluginMetadata;
    private String jarFileName;

    private int index;

    public abstract void initialize() throws Exception;
    public abstract void destroy() throws Exception;
}

