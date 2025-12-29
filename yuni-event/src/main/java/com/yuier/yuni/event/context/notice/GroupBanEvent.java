package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupBanEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:52
 * @description: 群禁言事件
 */

@Data
@NoArgsConstructor
public class GroupBanEvent extends YuniNoticeEvent {

    private String subType;
    private Long userId;
    private Long groupId;
    private Long operatorId;
    private Long duration;

    @Override
    public String toLogString() {
        String operatorLogStr = EventLogUtil.memberNameAndIdLogString(groupId, operatorId);
        String targetLogStr = 0L == userId ? "全体成员" : EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return operatorLogStr + "在 " + groupLogStr + "对 " + targetLogStr + "开启了禁言，时长 " + duration + " 秒";
    }
}
