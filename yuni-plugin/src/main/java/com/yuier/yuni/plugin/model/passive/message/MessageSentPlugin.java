package com.yuier.yuni.plugin.model.passive.message;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.YuniMessageSentEvent;
import com.yuier.yuni.event.detector.message.sent.MessageSentDetector;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;

/**
 * @Title: MessageSentPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive.message
 * @Date 2026/2/6 3:16
 * @description: 自身发送消息处理插件
 */

public abstract class MessageSentPlugin implements PassivePlugin<YuniMessageSentEvent, MessageSentDetector> {
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
    public MessageSentDetector getDetector() {
        return new MessageSentDetector();
    }

    @Override
    public UserPermission pluginPermission() {
        return UserPermission.USER;
    }
}
