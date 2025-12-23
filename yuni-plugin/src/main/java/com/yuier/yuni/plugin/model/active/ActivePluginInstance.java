package com.yuier.yuni.plugin.model.active;

import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: ActivePluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.active
 * @Date 2025/12/23 21:08
 * @description: 主动插件实体类
 */

@Data
@NoArgsConstructor
public class ActivePluginInstance implements PluginInstance {

    private ActivePlugin activePlugin;
    private PluginMetadata pluginMetadata;
    private String cronExpression;
    private Action action;

    @Override
    public YuniPlugin getPlugin() {
        return activePlugin;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
