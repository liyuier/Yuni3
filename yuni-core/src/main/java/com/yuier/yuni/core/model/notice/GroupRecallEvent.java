package com.yuier.yuni.core.model.notice;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.event.NoticeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: GroupRecallEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.notice
 * @Date 2025/12/29 17:08
 * @description: 群消息撤回事件
 */

@Getter
@Setter
@NoArgsConstructor
@PolymorphicSubType(value = "group_recall")
public class GroupRecallEvent extends NoticeEvent {

    // 群号
    private Long groupId;
    // 消息发送者 QQ 号
    private Long userId;
    // 操作者 QQ 号
    private Long operatorId;
    // 撤回的消息 ID
    private Long messageId;

    @Override
    public String toString() {
        return "GroupRecallEvent{" +
                "groupId=" + groupId +
                ", userId=" + userId +
                ", operatorId=" + operatorId +
                ", messageId=" + messageId +
                '}';
    }
}
