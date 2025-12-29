package com.yuier.yuni.event.detector.request;

import com.yuier.yuni.event.context.request.YuniRequestEvent;
import com.yuier.yuni.event.detector.message.YuniEventDetector;

/**
 * @Title: YuniRequestDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.request
 * @Date 2025/12/30 1:54
 * @description: 请求事件探测器
 */

public interface YuniRequestDetector extends YuniEventDetector<YuniRequestEvent> {

    YuniRequestEvent event();
}
