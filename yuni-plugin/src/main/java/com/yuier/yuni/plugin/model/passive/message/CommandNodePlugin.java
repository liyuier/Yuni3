package com.yuier.yuni.plugin.model.passive.message;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.detector.message.command.CommandNodeDetector;

/**
 * 命令触发的插件的抽象类 —— 使用 CommandNode 新命令系统。
 *
 * <p>插件继承此类并实现 {@link #getDetector()} 和 {@link #execute} 即可。</p>
 */
public abstract class CommandNodePlugin extends MessagePlugin<CommandNodeDetector> {

    @Override
    public UserPermission pluginPermission() {
        return getDetector().getRootNode().getPermission();
    }
}
