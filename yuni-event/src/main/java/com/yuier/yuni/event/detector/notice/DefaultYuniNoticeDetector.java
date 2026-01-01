package com.yuier.yuni.event.detector.notice;

import com.yuier.yuni.event.context.notice.YuniNoticeEvent;

/**
 * @Title: DefaultYuniNoticeDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.notice
 * @Date 2025/12/30 2:03
 * @description: 默认的通知事件探测器
 */

public class DefaultYuniNoticeDetector implements YuniNoticeDetector {

    public DefaultYuniNoticeDetector(YuniNoticeMatcher matcher) {
        this.matcher = matcher;
    }

    YuniNoticeMatcher matcher;
    YuniNoticeEvent yuniNoticeMatcher;

    @Override
    public Boolean match(YuniNoticeEvent event) {
        yuniNoticeMatcher = matcher.match(event);
        event.setYuniNoticeEvent(yuniNoticeMatcher);
        return yuniNoticeMatcher != null;
    }

    @Override
    public YuniNoticeEvent event() {
        return yuniNoticeMatcher;
    }
}
