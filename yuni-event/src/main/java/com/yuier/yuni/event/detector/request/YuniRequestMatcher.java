package com.yuier.yuni.event.detector.request;

import com.yuier.yuni.event.context.request.YuniRequestEvent;

/**
 * @Title: YuniRequestMatcher
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.request
 * @Date 2025/12/30 2:10
 * @description: 请求事件匹配器
 */

@FunctionalInterface
public interface YuniRequestMatcher {

    YuniRequestEvent match(YuniRequestEvent event);
}
