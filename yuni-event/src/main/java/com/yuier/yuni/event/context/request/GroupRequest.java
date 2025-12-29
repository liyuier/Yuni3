package com.yuier.yuni.event.context.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupRequest
 * @Author yuier
 * @Package com.yuier.yuni.event.context.request
 * @Date 2025/12/30 1:25
 * @description: 加群 / 申请入群请求
 */

@Data
public abstract class GroupRequest extends YuniRequestEvent {

    private String subType;

    private Long groupId;
}
