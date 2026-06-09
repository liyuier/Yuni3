package com.yuier.yuni.core.event.notice;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: FriendAddEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:56
 * @description: 好友添加事件
 */

@Data
@NoArgsConstructor
public class FriendAddEvent extends YuniNoticeEvent {

    private Long userId;

    @Override
    public String toLogString() {
        return "添加了好友: " + "用户(" + userId + ")";
    }
}
