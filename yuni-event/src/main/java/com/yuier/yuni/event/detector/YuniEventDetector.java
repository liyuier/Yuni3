package com.yuier.yuni.event.detector;

import com.yuier.yuni.event.context.SpringYuniEvent;

/**
 * @Title: YuniEventDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.model.detector
 * @Date 2025/12/22 22:35
 * @description: Yuni 事件探测器
 */

public interface YuniEventDetector<T extends SpringYuniEvent> {

    /**
     * @return  检查事件是否命中事件探测器
     */
    Boolean match(T event);
}
