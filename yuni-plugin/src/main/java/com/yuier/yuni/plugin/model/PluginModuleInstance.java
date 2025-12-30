package com.yuier.yuni.plugin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: PluginModuleInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/27 19:41
 * @description: 插件模块实例
 */

@Data
@NoArgsConstructor
public class PluginModuleInstance {

    private String jarFileName;

    private PluginModuleMetadata pluginModuleMetadata;

    private List<PluginInstance> pluginInstances;

    public String getModuleId() {
        return pluginModuleMetadata.getModuleId();
    }

    public String getModuleName() {
        return pluginModuleMetadata.getModuleName();
    }

}
