package com.yuier.yuni.event.model.message.detector.pattern;

import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.MessageDetector;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: PatternDetector
 * @Author yuier
 * @Package com.yuier.yuni.common.detect.message.pattern
 * @Date 2024/11/19 23:40
 * @description: 模式探测器
 */

@Data
@AllArgsConstructor
public class PatternDetector implements MessageDetector {

    private PatternMatcher pattern;

    @Override
    public Boolean match(YuniMessageEvent event) {
        return pattern.match(event.getMessageChain());
    }
}
