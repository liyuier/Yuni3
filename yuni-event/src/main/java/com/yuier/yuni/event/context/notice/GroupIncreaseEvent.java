package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupIncreaseEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:47
 * @description: 群成员增加事件
 */

@Data
@NoArgsConstructor
public class GroupIncreaseEvent extends YuniNoticeEvent {

    private String subType;
    private Long userId;
    private Long groupId;
    private Long operatorId;

    @Override
    public String toLogString() {
        String action = "approve".equals(subType) ? "同意" : "邀请";
        String memberLogStr = EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        String operatorLogStr = EventLogUtil.memberNameAndIdLogString(groupId, operatorId);
        return "群成员增加事件：" + operatorLogStr + action + memberLogStr + "进入 " + groupLogStr;
    }
}
