package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: PokeEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 1:07
 * @description: 群内戳一戳消息
 */

@Data
@NoArgsConstructor
public class PokeEvent extends YuniNoticeEvent {

    // 值为 poke
    private String subType;
    private Long userId;
    private Long groupId;
    private Long targetId;

    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String targetLogStr = EventLogUtil.memberNameAndIdLogString(groupId, targetId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return userLogStr + " 在" + groupLogStr + "戳了戳" + targetLogStr;
    }
}
