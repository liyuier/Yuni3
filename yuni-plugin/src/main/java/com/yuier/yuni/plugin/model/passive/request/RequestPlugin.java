package com.yuier.yuni.plugin.model.passive.request;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
import com.yuier.yuni.event.detector.request.DefaultYuniRequestDetector;
import com.yuier.yuni.plugin.event.PluginDisableEvent;
import com.yuier.yuni.plugin.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;

/**
 * @Title: RequestPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive.request
 * @Date 2026/1/5 21:37
 * @description: 请求事件处理
 */

public abstract class RequestPlugin implements PassivePlugin<YuniRequestEvent, DefaultYuniRequestDetector> {

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
