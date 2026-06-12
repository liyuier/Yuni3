package com.yuier.yuni.webapi.dto.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: PluginInfo
 * @Author yuier
 * @Package com.yuier.yuni.webapi.dto
 * @Date 2026/06/13
 * @description: 插件列表响应条目
 */

@Data
@AllArgsConstructor
public class PluginInfo {
    private String fullId;
    private String name;
    private String description;
    private String version;
    private String author;
    private String moduleId;
    private boolean builtIn;
    private String type;
}
