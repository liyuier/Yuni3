package com.yuier.yuni.event.context.request;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: AddGroupRequest
 * @Author yuier
 * @Package com.yuier.yuni.event.context.request
 * @Date 2025/12/30 1:29
 * @description: 入群请求
 */

@Data
@NoArgsConstructor
public class AddGroupRequest extends GroupRequest {

    // 值为 add
    private String subType;
    // 验证信息
    private Long groupId;

    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.userNameAndIdLogString(super.getUserId());
        String groupLogStr = EventLogUtil.groupNameAndIdLogString(super.getGroupId());
        return userLogStr + " 请求加群 " + groupLogStr + " ，验证信息: " + super.getComment();
    }
}
