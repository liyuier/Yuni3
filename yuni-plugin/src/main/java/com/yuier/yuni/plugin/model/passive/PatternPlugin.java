package com.yuier.yuni.plugin.model.passive;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.message.detector.pattern.PatternDetector;
import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;

/**
 * @Title: PatternPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive
 * @Date 2025/12/23 21:19
 * @description: 消息模式匹配触发的插件
 */

public abstract class PatternPlugin implements PassivePlugin<YuniMessageEvent, PatternDetector> {
    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void enable(PluginEnableEvent event) {

    }

    @Override
    public void disable(PluginDisableEvent event) {

    }

    @Override
    public UserPermission pluginPermission() {
        return UserPermission.USER;
    }
}
