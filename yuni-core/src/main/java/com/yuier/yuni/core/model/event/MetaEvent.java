package com.yuier.yuni.core.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.yuier.yuni.core.anno.PolymorphicSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: MetaEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
 * @Date 2025/12/29 16:58
 * @description: OneBot 的元事件
 */

@Getter
@Setter
@NoArgsConstructor
@PolymorphicSubType(value = "meta_event")
public class MetaEvent extends OneBotEvent{

    /**
     * 元事件类型
     * lifecycle：生命周期事件
     * heartbeat：心跳事件
     */
    @JsonProperty("meta_event_type")
    private String metaEventType;

    /**
     * 元事件子类型
     * lifecycle：生命周期事件
     *   - connect：WebSocket 连接成功；只有 ws 连接可以收到
     *   - enable：OneBot 启用；只有 HTTP POST 连接可以收到
     *   - disable：OneBot 停用；只有 HTTP POST 连接可以收到
     * heartbeat：心跳事件
     *   - heartbeat：心跳
     */
    private String subType;

    /* 心跳事件字段 */

    /**
     * 状态
     */
    private Object status;

    /**
     * 心跳间隔
     */
    private Long interval;
}
