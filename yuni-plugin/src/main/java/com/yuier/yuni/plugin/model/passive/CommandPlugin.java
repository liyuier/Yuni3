package com.yuier.yuni.plugin.model.passive;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.command.CommandDetector;

/**
 * @Title: CommandPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive
 * @Date 2025/12/23 21:16
 * @description: 命令触发的插件的抽象类
 */

public abstract class CommandPlugin implements PassivePlugin<YuniMessageEvent, CommandDetector> {
    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public UserPermission pluginPermission() {
        return getDetector().getCommandModel().getPermission();
    }
}
