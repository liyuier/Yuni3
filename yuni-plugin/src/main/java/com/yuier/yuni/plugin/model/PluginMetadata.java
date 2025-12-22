package com.yuier.yuni.plugin.model;

import lombok.Data;

import java.util.List;

/**
 * @Title: PluginMetadata
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:41
 * @description: 插件元数据模型类
 */

@Data
public class PluginMetadata {
    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private List<String> dependencies;
}
