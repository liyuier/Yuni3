package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: PluginContainer
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/24 16:47
 * @description: 插件容器
 */

@Component
public class PluginContainer {

    private final Map<String, ScheduledPluginInstance> activePlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> passivePlugins = new ConcurrentHashMap<>();

    /* TODO 重构接口 */
}
