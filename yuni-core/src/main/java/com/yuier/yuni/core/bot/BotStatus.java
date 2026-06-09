package com.yuier.yuni.core.bot;

/**
 * @Title: BotStatus
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 机器人账号连接状态
 */

public enum BotStatus {
    /** 连接建立中 */
    CONNECTING,
    /** 在线，可以收发消息 */
    ONLINE,
    /** 离线，未连接 */
    OFFLINE,
    /** 连接异常 */
    ERROR
}
