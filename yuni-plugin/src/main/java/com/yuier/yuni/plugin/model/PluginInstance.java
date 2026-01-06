package com.yuier.yuni.plugin.model;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.plugin.util.PluginBusinessMapUtil;
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

    private int index;

    public void initialize() {
        plugin.initialize();
    }

    public void destroy() {
        plugin.destroy();
    }

    public Boolean isBuiltIn() {
        return pluginMetadata != null && pluginMetadata.getBuildIn();
    }

    public YuniPluginType getPluginType() {
        return PluginBusinessMapUtil.getPluginType(plugin);
    }

    public String getPluginFullId() {
        return pluginMetadata.getFullId();
    }

    public String getPluginName() {
        return pluginMetadata.getName();
    }

    public String getPluginDescription() {
        return pluginMetadata.getDescription();
    }

    public Class<? extends YuniPlugin> getPluginClass() {
        return plugin.getClass();
    }
}


