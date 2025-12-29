package com.yuier.yuni.event.detector.message;

import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.event.context.YuniMessageEvent;

/**
 * @Title: MessageDetector
 * @Author yuier
 * @Package com.yuier.yuni.common.interfaces.detector
 * @Date 2024/11/9 16:29
 * @description: 消息探测器
 */

public interface MessageDetector extends YuniEventDetector<YuniMessageEvent> {

    MessageType listenAt();
}
