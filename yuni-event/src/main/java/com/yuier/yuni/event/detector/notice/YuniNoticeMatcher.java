package com.yuier.yuni.event.detector.notice;

import com.yuier.yuni.event.context.notice.YuniNoticeEvent;

/**
 * @Title: YuniNoticeMatcher
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.notice
 * @Date 2025/12/30 2:02
 * @description: 通知事件的匹配器
 */

@FunctionalInterface
public interface YuniNoticeMatcher {

    YuniNoticeEvent match(YuniNoticeEvent event);
}
