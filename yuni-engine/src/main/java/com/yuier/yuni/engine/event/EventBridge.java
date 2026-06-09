package com.yuier.yuni.engine.event;

import com.yuier.yuni.core.event.BotEventCallback;
import com.yuier.yuni.core.event.YuniEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @Title: EventBridge
 * @Author yuier
 * @Package com.yuier.yuni.engine.event
 * @Date 2025/12/22 17:56
 * @description: 实现 BotEventCallback，将适配器装配好的 YuniEvent 通过 Spring Event 机制发布。
 *               适配器从原始 JSON → 协议模型 → 业务模型的完整翻译已在适配器内部完成，
 *               EventBridge 仅负责将事件发布到 Spring 事件总线。
 */

@Slf4j
@Component
public class EventBridge implements BotEventCallback {

    @Autowired
    ApplicationEventPublisher springPublisher;

    @Override
    public void onEvent(YuniEvent event) {
        springPublisher.publishEvent(event);
    }
}
