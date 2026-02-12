package com.yuier.yuni.event.detector.notice;

import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.detector.YuniEventDetector;

/**
 * @Title: YuniNoticeDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.notice
 * @Date 2025/12/30 1:54
 * @description: 通知事件探测器
 */

public interface YuniNoticeDetector extends YuniEventDetector<YuniNoticeEvent> {

    YuniNoticeEvent event();
}
