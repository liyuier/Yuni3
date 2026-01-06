package com.yuier.yuni.plugin.model.active;

import com.yuier.yuni.plugin.model.PluginInstance;
import lombok.Data;

/**
 * @Title: ActivePluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.active
 * @Date 2025/12/25 0:17
 * @description: 主动插件抽象基类
 */

@Data
public abstract class ActivePluginInstance extends PluginInstance {

    private Action action;

}
