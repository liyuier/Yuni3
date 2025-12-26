package com.yuier.yuni.plugin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.jar.JarFile;

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
    private String id;
    /**
     * 插件名称，显示给用户
     */
    private String name;
    /**
     * 插件版本
     */
    private String version;
    /**
     * 插件描述，会提供给用户
     */
    private String description;
    /**
     * 插件作者。尊重知识版权说是
     */
    private String author;
    /**
     * 插件依赖的插件 ID ，实际上目前并没有实现依赖检查() TODO
     */
    private List<String> dependencies;
    /**
     * 插件默认是否启用 true 为默认启用，false 为默认禁用
     */
    private Boolean defaultEnable;

    // TODO 待实现的字段

    // 插件模块名
    private String moduleName;

    /**
     * 提供的功能的类型（娱乐、学习、工具）
     */
    private String functionType;

    /**
     * 关键词
     */
    List<String> keyWords;

    /**
     * 插件图标
     */
    private String icon;

    /**
     * 插件主页
     */
    private String homePage;

    /**
     * 源码地址
     */
    private String sourceCodeAddress;

    // TODO
}
