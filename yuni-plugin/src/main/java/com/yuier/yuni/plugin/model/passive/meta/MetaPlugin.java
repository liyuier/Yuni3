package com.yuier.yuni.plugin.model.passive.meta;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.detector.meta.DefaultYuniMetaDetector;
import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;

/**
 * @Title: MetaPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive.meta
 * @Date 2026/1/5 21:26
 * @description: 元事件
 */

public abstract class MetaPlugin implements PassivePlugin<YuniMetaEvent, DefaultYuniMetaDetector> {
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
