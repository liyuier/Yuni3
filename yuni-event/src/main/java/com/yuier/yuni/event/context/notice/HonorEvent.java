package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: HonorEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 1:13
 * @description: 群荣誉变更事件
 */

@Data
@NoArgsConstructor
public class HonorEvent extends YuniNoticeEvent {

    // 值为 honor
    private String subType;
    private Long userId;
    private Long groupId;
    /**
     * 荣誉类型
     * - group_honor: 群成员荣誉变更
     *   - talkative: 龙王
     *   - performer: 群聊之火
     *   - emotion: 群聊炽火
     */
    private String HonorType;

    @Override
    public String toLogString() {
        String honorLogStr = switch (HonorType) {
            case "talkative" -> "龙王";
            case "performer" -> "群聊之火";
            case "emotion" -> "群聊炽火";
            default -> "未知";
        };
        String userLogStr = EventLogUtil.memberNameAndIdLogString(groupId, userId);
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(groupId);
        return userLogStr + " 在群聊 " + groupLogStr + "获得了" + honorLogStr + "头衔";
    }
}
