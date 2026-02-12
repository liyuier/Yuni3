package com.yuier.yuni.event.detector.meta;

import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.detector.YuniEventDetector;

/**
 * @Title: YuniMetaDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.meta
 * @Date 2025/12/30 1:53
 * @description: 元事件探测器接口
 */

public interface YuniMetaDetector extends YuniEventDetector<YuniMetaEvent> {

    YuniMetaEvent event();
}
