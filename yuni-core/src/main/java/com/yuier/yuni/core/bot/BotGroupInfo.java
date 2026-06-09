package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: BotGroupInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 群组基本信息（协议无关）
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotGroupInfo {
    private Long groupId;
    private String groupName;
    private String groupMemo;
    private Integer memberCount;
    private Integer maxMemberCount;
    private Long groupCreateTime;
}
