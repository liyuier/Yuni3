package com.yuier.yuni.event.context.meta;

import lombok.Data;

/**
 * @Title: YuniLifecycleEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.meta
 * @Date 2025/12/29 19:20
 * @description:
 */

@Data
public class YuniLifecycleEvent extends YuniMetaEvent{
    @Override
    public String toLogString() {
        return "收到 OneBot 生命周期事件: " + super.getSubType();
    }
}
