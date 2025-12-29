package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupRecallEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
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
        String operatorLogStr = EventLogUtil.memberNameAndIdLogString(groupId, operatorId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return operatorLogStr + " 在 " + groupLogStr + " 撤回了一条消息: id " + messageId;
    }
}
