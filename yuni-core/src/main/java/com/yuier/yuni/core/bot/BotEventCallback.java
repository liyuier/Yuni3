package com.yuier.yuni.core.bot;

/**
 * @Title: BotEventCallback
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 业务层暴露给适配器的事件回调接口。
 *               适配器收到平台事件推送后，调用此回调通知业务层。
 */

@FunctionalInterface
public interface BotEventCallback {

    /**
     * 适配器收到平台事件推送时调用
     * @param event 平台事件
     */
    void onEvent(YuniPlatformEvent event);
}
