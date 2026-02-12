package com.yuier.yuni.event.detector.message.sent;

import com.yuier.yuni.event.context.YuniMessageSentEvent;
import com.yuier.yuni.event.detector.YuniEventDetector;

/**
 * @Title: MessageSentDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.detector.message.sent
 * @Date 2026/2/6 3:18
 * @description: 自身发送消息探测器
 */

public class MessageSentDetector implements YuniEventDetector<YuniMessageSentEvent> {
    @Override
    public Boolean match(YuniMessageSentEvent event) {
        return true;
    }
}
