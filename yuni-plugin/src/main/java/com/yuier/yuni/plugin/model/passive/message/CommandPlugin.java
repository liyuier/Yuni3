package com.yuier.yuni.plugin.model.passive.message;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.detector.message.command.CommandDetector;

/**
 * @Title: CommandPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive
 * @Date 2025/12/23 21:16
 * @description: 命令触发的插件的抽象类
 */

public abstract class CommandPlugin extends MessagePlugin<CommandDetector> {

    @Override
    public UserPermission pluginPermission() {
        return getDetector().getCommandModel().getPermission();
    }
}
