package com.yuier.yuni.event.detector.meta;

import com.yuier.yuni.event.context.meta.YuniMetaEvent;

/**
 * @Title: YuniMetaMatcher
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.meta
 * @Date 2025/12/30 1:57
 * @description: 元事件匹配器
 */

@FunctionalInterface
public interface YuniMetaMatcher {

    YuniMetaEvent match(YuniMetaEvent event);
}
