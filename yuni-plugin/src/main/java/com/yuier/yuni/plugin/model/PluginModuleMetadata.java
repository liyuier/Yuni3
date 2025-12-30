package com.yuier.yuni.plugin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: PluginModule
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/27 19:28
 * @description: 插件模块工程
 */

@Data
@NoArgsConstructor
public class PluginModuleMetadata {

    /**
     * 插件模块 ID
     */
    @JsonProperty("module_id")
    private String moduleId;

    /**
     * 插件模块名
     */
    @JsonProperty("module_name")
    private String moduleName;

    /**
     * 插件列表
     */
    @JsonProperty("plugins")
    List<PluginMetadata> plugins;
}
