package com.yuier.yuni.webapi.dto.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 插件模块信息，包含模块标识、模块名及该模块下的所有插件。
 */
@Data
@AllArgsConstructor
public class PluginModuleInfo {
    /** 模块ID，对应 JAR 包内 module.json 中的 module_id */
    private String moduleId;
    /** 模块名，对应 JAR 包内 module.json 中的 module_name */
    private String moduleName;
    /** 该模块下的插件列表 */
    private List<PluginInfo> plugins;
}
