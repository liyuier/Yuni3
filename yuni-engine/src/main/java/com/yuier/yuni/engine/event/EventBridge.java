package com.yuier.yuni.engine.event;

import com.yuier.yuni.core.constants.OneBotPostType;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.BeanCopyUtils;
import com.yuier.yuni.engine.manager.context.RequestContextManager;
import com.yuier.yuni.event.model.context.ChatSession;
import com.yuier.yuni.event.model.context.SpringYuniEvent;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @Title: EventBridge
 * @Author yuier
 * @Package com.yuier.yuni.engine.event
 * @Date 2025/12/22 17:56
 * @description: 桥接 OneBot 事件和 Spring 事件系统
 */

@Component
public class EventBridge {

    @Autowired
    ApplicationEventPublisher springPublisher;

    public void publishRawEvent(OneBotEvent oneBotEvent) {
        // 将 OneBot 事件转为 Yuni 内部事件
        SpringYuniEvent springYuniEvent = convertToYuniEvent(oneBotEvent);
        // 借助 Spring 的 EventListener 机制发布事件
        springPublisher.publishEvent(springYuniEvent);
    }

    private SpringYuniEvent convertToYuniEvent(OneBotEvent event) {
        switch (event.getPostType()) {
            case OneBotPostType.MESSAGE:
                return buildYuniMessageEvent(event);
            default:
                throw new RuntimeException("未知上报事件类型！");
        }
    }

    private YuniMessageEvent buildYuniMessageEvent(OneBotEvent event) {
        MessageEvent messageEvent = (MessageEvent) event;
        YuniMessageEvent yuniMessageEvent = BeanCopyUtils.copyBean(messageEvent, YuniMessageEvent.class);
        yuniMessageEvent.setMessage(messageEvent.getMessage());
        yuniMessageEvent.setMessageChain(new MessageChain(yuniMessageEvent.getMessage()));
        yuniMessageEvent.setSender(messageEvent.getSender());
        yuniMessageEvent.setMessageEvent(messageEvent);
        // 为消息事件添加 chatSession
        ChatSession chatSession = RequestContextManager.getChatSession();
        assert chatSession != null;
        chatSession.setSelfId(messageEvent.getSelfId());
        chatSession.setUserId(messageEvent.getUserId());
        chatSession.setGroupId(messageEvent.getGroupId());
        chatSession.setMessageType(messageEvent.getMessageType());
        // TODO 添加 OneBotBaseUrl
        yuniMessageEvent.setSession(chatSession);
        return yuniMessageEvent;
    }
}
