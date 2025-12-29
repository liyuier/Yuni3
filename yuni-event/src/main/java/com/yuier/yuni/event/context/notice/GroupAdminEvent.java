package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupAdminEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:26
 * @description: 群管理员变动事件
 */

@Data
@NoArgsConstructor
public class GroupAdminEvent extends YuniNoticeEvent {

    private String subType;
    private Long userId;
    private Long groupId;

    @Override
    public String toLogString() {
        String action = subType.equals("set") ? "被设置管理员" : "被取消管理员";
        return "群管理员变动事件: " + EventLogUtil.memberAtGroupLogString(userId, groupId) + action;
    }
}
