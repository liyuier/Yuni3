package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: BotMessageInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 历史消息信息（协议无关）
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotMessageInfo {
    private Long messageId;
    private Long userId;
    private Long groupId;
    private String messageType;
}
