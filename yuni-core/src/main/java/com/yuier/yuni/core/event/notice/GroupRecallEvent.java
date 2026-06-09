package com.yuier.yuni.core.event.notice;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupRecallEvent
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot.model
 * @Date 2025/12/30 1:04
 * @description:
 */

@Data
@NoArgsConstructor
public class GroupRecallEvent extends YuniNoticeEvent {

    private Long userId;
    private Long groupId;
    private Long operatorId;
    private Long messageId;

    @Override
    public String toLogString() {
        String operatorLogStr = "成员(" + operatorId + ")";
        String groupLogStr = "群(" + groupId + ")";
        return operatorLogStr + " 在 " + groupLogStr + " 撤回了一条消息: id " + messageId;
    }
}
