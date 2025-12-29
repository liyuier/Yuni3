package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupDecreaseEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:41
 * @description: 群成员减少事件
 */

@Data
@NoArgsConstructor
public class GroupDecreaseEvent extends YuniNoticeEvent {

    private String subType;
    private Long userId;
    private Long groupId;
    private Long operatorId;

    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String operatorLogStr = EventLogUtil.memberNameAndIdLogString(groupId, operatorId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return switch (subType) {
            case "leave" -> "群成员减少事件: " + userLogStr + "离开了 " + groupLogStr;
            case "kick" -> "群成员减少事件: " + userLogStr + "被 " + operatorLogStr + "踢出了 " + groupLogStr;
            case "kick_me" -> "群成员减少事件: 机器人登录号被 " + operatorLogStr + "踢出了 " + groupLogStr;
            case null, default -> "群成员减少事件: 未知 subType: " + subType;
        };
    }
}
