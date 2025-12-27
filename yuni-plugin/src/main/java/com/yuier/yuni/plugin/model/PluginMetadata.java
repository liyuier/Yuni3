package com.yuier.yuni.plugin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: PluginMetadata
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:41
 * @description: 插件元数据模型类
 */

@Data
@NoArgsConstructor
public class PluginMetadata {
    /**
     * 插件 ID，当前实现为 模块名-插件全限定名
     */
    @JsonProperty("id")
    private String id;
    /**
     * 插件名称，显示给用户
     */
    @JsonProperty("name")
    private String name;
    /**
     * 插件版本
     */
    @JsonProperty("version")
    private String version;
    /**
     * 插件描述，会提供给用户
     */
    @JsonProperty("description")
    private String description;
    /**
     * 插件作者。尊重知识版权说是
     */
    @JsonProperty("author")
    private String author;
    /**
     * 插件依赖的插件 ID ，实际上目前并没有实现依赖检查() TODO
     */
    @JsonProperty("dependencies")
    private List<String> dependencies;
    /**
     * 插件默认是否启用 true 为默认启用，false 为默认禁用
     */
    @JsonProperty("default_enable")
    private Boolean defaultEnable = true;

    // TODO 待实现的字段

    // 是否为系统内置插件
    @JsonProperty("build_in")
    private Boolean buildIn = false;

    // 插件模块名
    private String moduleName;

    /**
     * 提供的功能的类型（娱乐、学习、工具）
     */
    private String functionType;

    /**
     * 关键词
     */
    @JsonProperty("keywords")
    List<String> keywords;

    /**
     * 插件图标
     */
    @JsonProperty("icon")
    private String icon;

    /**
     * 插件主页
     */
    @JsonProperty("homepage")
    private String homepage;

    /**
     * 源码地址
     */
    @JsonProperty("source_code_address")
    private String sourceCodeAddress;

    // TODO
}
