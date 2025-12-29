package com.yuier.yuni.core.model.event;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: RequestEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
 * @Date 2025/12/29 17:07
 * @description: 请求事件
 */

@Getter
@Setter
@NoArgsConstructor
@PolymorphicSubType
public class RequestEvent extends OneBotEvent {
    /**
     * 请求类型
     *   - friend 添加好友请求
     *   - group 添加群请求
     */
    private String requestType;
}
