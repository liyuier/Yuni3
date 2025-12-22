package com.yuier.yuni.engine.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: SpringYuniEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event.spring
 * @Date 2025/12/22 17:14
 * @description: Spring 内的 event 模型
 */

@Getter
@Setter
@NoArgsConstructor
public abstract class SpringYuniEvent {

    // 事件发生的时间戳
    private Long time;

    // 收到消息的机器人 QQ 号
    private Long selfId;

    /**
     * 事件类型。此处用于标注子类
     * message：消息事件
     * notice：通知事件
     * request：请求事件
     * meta_event：元事件
     */
    private String postType;
}
