package com.yuier.yuni.plugin.model;

import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;

/**
 * @Title: YuniPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:41
 * @description: Yuni 机器人插件接口
 */


public interface YuniPlugin {

    /**
     * 插件初始化
     */
    void initialize();

    /**
     * 插件销毁
     */
    void destroy();

    /**
     * 插件启用
     * @param event 插件启用事件
     */
    void enable(PluginEnableEvent event);

    /**
     * 插件禁用
     * @param event 插件禁用事件
     */
    void disable(PluginDisableEvent event);
}
