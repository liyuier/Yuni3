package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

    private final List<String> pluginModuleIds = new ArrayList<>();

    private final Map<Integer, String> pluginIndexToIdMap = new ConcurrentHashMap<>();

    private final Map<String, PluginModuleInstance> pluginModules = new ConcurrentHashMap<>();

    private final Map<String, ActivePluginInstance> activePlugins = new ConcurrentHashMap<>();

    private final Map<String, PassivePluginInstance> commandPlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> patternPlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> noticePlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> requestPlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> metaPlugins = new ConcurrentHashMap<>();

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
        passivePlugin = noticePlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        passivePlugin = requestPlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        passivePlugin = metaPlugins.get(pluginId);
        if (passivePlugin != null) {
            return passivePlugin;
        }
        return null;
    }

    public PluginModuleInstance getPluginModuleById(String moduleId) {
        return pluginModules.get(moduleId);
    }

    public String getPluginIdByIndex(int index) {
        return pluginIndexToIdMap.get(index);
    }

    public PluginInstance getPluginInstanceByIndex(int index) {
        String pluginId = getPluginIdByIndex(index);
        return getPluginInstanceById(pluginId);
    }

    public void clear() {
        pluginModuleIds.clear();
        pluginIndexToIdMap.clear();
        pluginModules.clear();
        activePlugins.clear();
        commandPlugins.clear();
        patternPlugins.clear();
        noticePlugins.clear();
        requestPlugins.clear();
        metaPlugins.clear();
    }
}
