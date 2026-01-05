package com.yuier.yuni.plugin.model.passive.notice;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.detector.notice.DefaultYuniNoticeDetector;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;

/**
 * @Title: NoticePlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.passive.notice
 * @Date 2026/1/1 16:31
 * @description: 请求事件处理插件
 */

public abstract class NoticePlugin implements PassivePlugin<YuniNoticeEvent, DefaultYuniNoticeDetector> {

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
