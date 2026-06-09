package com.yuier.yuni.core.bot;

/**
 * @Title: YuniBotFactory
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: YuniBot 工厂 SPI。
 *               各适配器模块实现此接口并注册为 Spring Bean，
 *               自动配置根据 yuni.protocol.type 选择对应工厂来创建 YuniBot。
 *               例如："onebot" → OneBotYuniBotFactory。
 */

public interface YuniBotFactory {

    /**
     * 工厂支持的协议/平台类型标识
     * @return 如 "onebot"
     */
    String getProtocolType();

    /**
     * 创建 YuniBot 实例
     * @return YuniBot 实例
     */
    YuniBot createBot();
}
