package com.yuier.yuni.plugin.model.active;

import com.yuier.yuni.plugin.model.AbstractPluginInstance;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.Data;

/**
 * @Title: ActivePluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.active
 * @Date 2025/12/25 0:17
 * @description: 主动插件抽象基类
 */

@Data
public abstract class ActivePluginInstance extends AbstractPluginInstance {

    private ActivePlugin plugin;
    private PluginMetadata pluginMetadata;
    private Action action;

    @Override
    public YuniPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
