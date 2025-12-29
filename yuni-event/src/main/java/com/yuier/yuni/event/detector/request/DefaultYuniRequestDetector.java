package com.yuier.yuni.event.detector.request;

import com.yuier.yuni.event.context.request.YuniRequestEvent;

/**
 * @Title: DefaultYuniRequestDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.request
 * @Date 2025/12/30 2:11
 * @description: 默认请求事件探测器
 */
public class DefaultYuniRequestDetector implements YuniRequestDetector {

    private YuniRequestMatcher matcher;
    private YuniRequestEvent yuniRequestMatcher;

    @Override
    public Boolean match(YuniRequestEvent event) {
        yuniRequestMatcher = matcher.match(event);
        return null;
    }

    @Override
    public YuniRequestEvent event() {
        return yuniRequestMatcher;
    }
}
