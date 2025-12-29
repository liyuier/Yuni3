package com.yuier.yuni.event.context.request;

import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: AddFriendRequest
 * @Author yuier
 * @Package com.yuier.yuni.event.context.request
 * @Date 2025/12/30 1:24
 * @description: 添加好友请求
 */

@Data
@NoArgsConstructor
public class AddFriendRequest extends YuniRequestEvent {

    @Override
    public String toLogString() {
        String userLogStr = EventLogUtil.userNameAndIdLogString(super.getUserId());
        return userLogStr + " 请求添加 bot 好友，验证信息: " + super.getComment();
    }
}
