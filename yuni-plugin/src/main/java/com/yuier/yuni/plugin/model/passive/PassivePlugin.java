package com.yuier.yuni.plugin.model.passive;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.SpringYuniEvent;
import com.yuier.yuni.event.message.detector.YuniEventDetector;
import com.yuier.yuni.plugin.model.YuniPlugin;

/**
 * @Title: PassivePlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:48
 * @description: 被动触发的插件
 */

public interface PassivePlugin<T extends SpringYuniEvent, S extends YuniEventDetector<?>> extends YuniPlugin {

    /**
     * 获取事件探测器
     * @return  事件探测器
     */
    S getDetector();

    /**
     * 插件执行方法
     * @param eventContext  事件上下文
     */
    void execute(T eventContext);

    UserPermission pluginPermission();
}
