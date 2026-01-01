package com.yuier.yuni.event.detector.meta;

import com.yuier.yuni.event.context.meta.YuniMetaEvent;

/**
 * @Title: DefaultYuniMetaDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.meta
 * @Date 2025/12/30 1:56
 * @description: 元事件探测器的标准实现
 */

public class DefaultYuniMetaDetector implements YuniMetaDetector {

    public DefaultYuniMetaDetector(YuniMetaMatcher matcher) {
        this.matcher = matcher;
    }

    private YuniMetaMatcher matcher;
    private YuniMetaEvent yuniMetaEvent;

    @Override
    public Boolean match(YuniMetaEvent event) {
        yuniMetaEvent = matcher.match(event);
        event.setYuniMetaEvent(yuniMetaEvent);
        return yuniMetaEvent != null;
    }

    @Override
    public YuniMetaEvent event() {
        return yuniMetaEvent;
    }
}
