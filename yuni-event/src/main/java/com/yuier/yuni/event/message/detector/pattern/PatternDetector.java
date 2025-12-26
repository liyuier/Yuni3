package com.yuier.yuni.event.message.detector.pattern;

import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.event.message.detector.MessageDetector;
import com.yuier.yuni.event.context.YuniMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.yuier.yuni.core.constants.OneBotMessageType.GROUP_MESSAGE;
import static com.yuier.yuni.core.constants.OneBotMessageType.PRIVATE_MESSAGE;

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
        return messageTypeMatches(event) && pattern.match(event.getMessageChain());
    }

    @Override
    public MessageType listenAt() {
        return MessageType.GROUP;
    }

    public Boolean messageTypeMatches(YuniMessageEvent event) {
        return (listenAt() == MessageType.GROUP && event.getMessageType().equals(GROUP_MESSAGE)) ||
                (listenAt() == MessageType.PRIVATE && event.getMessageType().equals(PRIVATE_MESSAGE));
    }
}
