package com.yuier.yuni.core.event;

/**
 * @Title: BotEventCallback
 * @Author yuier
 * @Package com.yuier.yuni.event.context
 * @Date 2026/06/09
 * @description: 业务层暴露给适配器的事件回调接口。
 *               适配器收到平台事件推送后，完成协议翻译，
 *               将装配好的 YuniEvent 通过此回调传递给业务层。
 */

@FunctionalInterface
public interface BotEventCallback {
    void onEvent(YuniEvent event);
}
