package com.yuier.yuni.event.context.request;

import com.yuier.yuni.event.context.SpringYuniEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: YuniRequestEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.request
 * @Date 2025/12/29 19:15
 * @description: 请求事件
 */

@Data
@NoArgsConstructor
public class YuniRequestEvent extends SpringYuniEvent {

    /**
     * 请求类型
     *   - friend 添加好友请求
     *   - group 加群请求 / 邀请登录号入群请求
     */
    private String requestType;

    // 验证信息
    private String comment;

    // 发送请求的 QQ 号
    private Long userId;

    // 请求 flag，在调用处理请求的 API 时需要
    private String flag;

    // 群号（加群请求时）
    private Long groupId;

    /** 匹配到的具体请求事件子类型（由 detector 设置） */
    private YuniRequestEvent matchedEvent;

    @Override
    public String toLogString() {
        return "收到未定义请求事件。";
    }
}
