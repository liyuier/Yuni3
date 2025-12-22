package com.yuier.yuni.plugin.model;

import com.yuier.yuni.core.model.event.OneBotEvent;

/**
 * @Title: YuniPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:41
 * @description: Yuni 机器人插件接口
 */


public interface YuniPlugin {

    /**
     * 获取插件元数据
     */
    PluginMetadata getMetadata();

    /**
     * 插件初始化
     */
    void initialize();

    /**
     * 插件销毁
     */
    void destroy();
}
