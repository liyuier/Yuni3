package com.yuier.yuni.event.context.meta;

import lombok.Data;

/**
 * @Title: YuniHeartbeatEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.meta
 * @Date 2025/12/29 19:29
 * @description:
 */

@Data
public class YuniHeartbeatEvent extends YuniMetaEvent{

    /**
     * 状态
     */
    private Object status;

    /**
     * 心跳间隔
     */
    private Long interval;

    @Override
    public String toLogString() {
        return "收到心跳事件，距离下次心跳还有 " + interval / 1000 + " 秒";
    }
}
