package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: BotGroupMemberInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 群成员信息（协议无关）
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotGroupMemberInfo {
    private Long groupId;
    private Long userId;
    private String nickname;
    private String card;
    private String role;
    private String title;
}
