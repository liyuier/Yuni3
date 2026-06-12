package com.yuier.yuni.webapi.dto.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 插件列表响应条目
 */
@Data
@AllArgsConstructor
public class PluginInfo {
    private String fullId;
    private String name;
    private String description;
    private List<String> tips;
    private String version;
    private String author;
    private String moduleId;
    private boolean builtIn;
    private String type;
}
