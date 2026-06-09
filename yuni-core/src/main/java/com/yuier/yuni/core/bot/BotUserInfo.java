package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: BotUserInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 用户信息（协议无关）
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotUserInfo {
    private Long userId;
    private String nickname;
}
