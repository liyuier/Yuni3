package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: LuckyKingEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 1:10
 * @description: 群红包运气王
 */

@Data
@NoArgsConstructor
public class LuckyKingEvent extends YuniNoticeEvent {

    // 值为 lucky_king
    private String subType;
    // 红包发送者 QQ 号
    private Long userId;
    private Long groupId;
    // 运气王 QQ 号
    private Long targetId;


    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String targetLogStr = EventLogUtil.memberNameAndIdLogString(groupId, targetId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return userLogStr + " 在" + groupLogStr + "发送了红包，运气王是 " + targetLogStr;
    }
}
