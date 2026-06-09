package com.yuier.yuni.core.bot;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: YuniBotConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 机器人账号配置基类。
 *               具体平台（OneBot、Telegram 等）可继承扩展。
 */

@Data
@NoArgsConstructor
public class YuniBotConfig {

    /** 平台标识，如 "onebot" */
    private String platform;

    /** 机器人账号 ID（QQ 号等） */
    private String botId;
}
