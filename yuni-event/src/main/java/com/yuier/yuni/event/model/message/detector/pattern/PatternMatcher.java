package com.yuier.yuni.event.model.message.detector.pattern;

import com.yuier.yuni.core.model.message.MessageChain;

/**
 * @Title: PatternMatcher
 * @Author yuier
 * @Package com.yuier.yuni.common.interfaces.detector.message.pattern
 * @Date 2024/11/19 23:41
 * @description: 消息模式匹配器
 */

@FunctionalInterface
public interface PatternMatcher {

    Boolean match(MessageChain chain);
}
