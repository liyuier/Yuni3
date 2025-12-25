package com.yuier.yuni.plugin.model;

import lombok.Data;

/**
 * @Title: AbstractPluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/25 18:00
 * @description: 插件示例抽象基类
 */

@Data
public abstract class AbstractPluginInstance implements PluginInstance{

    private String jarFileName;

    @Override
    public String getJarFileName() {
        return jarFileName;
    }
}
