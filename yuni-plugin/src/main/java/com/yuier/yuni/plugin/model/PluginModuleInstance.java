package com.yuier.yuni.plugin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: PluginModuleInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2026/1/6 3:28
 * @description: 新插件模块实例，用于重构
 */

@Data
@NoArgsConstructor
public class PluginModuleInstance {

    // 这里假设 jar 包都直接放在插件目录下，不创建子目录
    private String jarFileName;

    private PluginModuleMetadata pluginModuleMetadata;

    public String getModuleId() {
        return pluginModuleMetadata.getModuleId();
    }

    public String getModuleName() {
        return pluginModuleMetadata.getModuleName();
    }

    public List<String> getPluginFullIds() {
        ArrayList<String> pluginFullIds = new ArrayList<>();
        pluginModuleMetadata.getPlugins().forEach(pluginMetadata -> pluginFullIds.add(pluginMetadata.getFullId()));
        return pluginFullIds;
    }

    public List<PluginMetadata> getPluginMetadataList() {
        return pluginModuleMetadata.getPlugins();
    }
}
