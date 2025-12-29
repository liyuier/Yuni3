package com.yuier.yuni.event.context.request;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: InviteToGroupRequest
 * @Author yuier
 * @Package com.yuier.yuni.event.context.request
 * @Date 2025/12/30 1:32
 * @description: 邀请入群请求
 */

@Data
@NoArgsConstructor
public class InviteToGroupRequest extends GroupRequest {

    // 值为 add
    private String subType;
    // 验证信息
    private Long groupId;
    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.userNameAndIdLogString(super.getUserId());
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(super.getGroupId());
        return userLogStr + " 邀请 bot 入群 " + groupLogStr + " ，验证信息: " + super.getComment();
    }
}
