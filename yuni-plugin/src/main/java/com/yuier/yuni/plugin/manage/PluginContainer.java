package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: PluginContainer
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/24 16:47
 * @description: 插件容器
 */

@Data
@Component
public class PluginContainer {

    private final Map<String, PluginModuleInstance> pluginModules = new ConcurrentHashMap<>();

    private final Map<String, ActivePluginInstance> activePlugins = new ConcurrentHashMap<>();

    private final Map<String, PassivePluginInstance> commandPlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> patternPlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> noticeAndRequestPlugins = new ConcurrentHashMap<>();

    public PluginInstance getPluginInstanceById(String pluginId) {
        ActivePluginInstance activePlugin = activePlugins.get(pluginId);
        if (activePlugin != null) {
            return activePlugin;
        }
        PassivePluginInstance passivePlugin = commandPlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        passivePlugin = patternPlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        passivePlugin = noticeAndRequestPlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        return null;
     }
}
