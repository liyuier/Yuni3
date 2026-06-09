package com.yuier.yuni.core.event.request;

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
        String userLogStr = "用户(" + super.getUserId() + ")";
        String groupLogStr = "群(" + super.getGroupId() + ")";
        return userLogStr + " 请求加群 " + groupLogStr + " ，验证信息: " + super.getComment();
    }
}
