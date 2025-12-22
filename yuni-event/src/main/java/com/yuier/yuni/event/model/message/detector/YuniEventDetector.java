package com.yuier.yuni.event.model.message.detector;

import com.yuier.yuni.event.model.context.SpringYuniEvent;

/**
 * @Title: YuniEventDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.model.detector
 * @Date 2025/12/22 22:35
 * @description: Yuni 事件探测器
 */

public interface YuniEventDetector<T extends SpringYuniEvent> {

    /**
     * @return  检查消息是否命中消息探测器
     */
    Boolean match(T event);
}
