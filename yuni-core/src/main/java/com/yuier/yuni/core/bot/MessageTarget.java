package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: MessageTarget
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 消息发送目标，封装目标类型与目标 ID
 */

@Data
@AllArgsConstructor
public class MessageTarget {

    /** 目标类型：群聊 / 私聊 */
    private TargetType targetType;

    /** 目标 ID（群号或用户 QQ 号） */
    private Long targetId;

    /** 源消息 ID（回复场景下使用，可选） */
    private String sourceMessageId;

    public enum TargetType {
        GROUP,
        PRIVATE
    }

    /** 快捷工厂：群聊消息目标 */
    public static MessageTarget group(Long groupId) {
        return new MessageTarget(TargetType.GROUP, groupId, null);
    }

    /** 快捷工厂：私聊消息目标 */
    public static MessageTarget privateChat(Long userId) {
        return new MessageTarget(TargetType.PRIVATE, userId, null);
    }

    /** 快捷工厂：带引用消息的群聊目标 */
    public static MessageTarget group(Long groupId, String sourceMessageId) {
        return new MessageTarget(TargetType.GROUP, groupId, sourceMessageId);
    }

    /** 快捷工厂：带引用消息的私聊目标 */
    public static MessageTarget privateChat(Long userId, String sourceMessageId) {
        return new MessageTarget(TargetType.PRIVATE, userId, sourceMessageId);
    }
}
