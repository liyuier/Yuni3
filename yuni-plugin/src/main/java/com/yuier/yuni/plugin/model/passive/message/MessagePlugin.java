package com.yuier.yuni.plugin.model.passive.message;

import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.detector.message.MessageDetector;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;

/**
 * @Title: MessagePlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive.message
 * @Date 2025/12/27 22:48
 * @description: 消息插件
 */

public abstract class MessagePlugin<T extends MessageDetector> implements PassivePlugin<YuniMessageEvent, T> {

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
}
