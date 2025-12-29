package com.yuier.yuni.core.model.event;

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
}
