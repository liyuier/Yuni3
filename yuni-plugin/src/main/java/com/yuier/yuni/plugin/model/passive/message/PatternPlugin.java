package com.yuier.yuni.plugin.model.passive.message;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.detector.message.pattern.PatternDetector;

/**
 * @Title: PatternPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive
 * @Date 2025/12/23 21:19
 * @description: 消息模式匹配触发的插件
 */

public abstract class PatternPlugin extends MessagePlugin<PatternDetector> {

    @Override
    public UserPermission pluginPermission() {
        return UserPermission.USER;
    }
}
