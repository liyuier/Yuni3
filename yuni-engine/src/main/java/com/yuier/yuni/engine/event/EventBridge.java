package com.yuier.yuni.engine.event;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.constants.OneBotPostType;
import com.yuier.yuni.core.model.event.*;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.BeanCopyUtils;
import com.yuier.yuni.event.context.ChatSession;
import com.yuier.yuni.event.context.SpringYuniEvent;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class EventBridge {

    @Autowired
    ApplicationEventPublisher springPublisher;
    @Autowired
    OneBotAdapter adapter;

    public void publishRawEvent(OneBotEvent oneBotEvent) {
        // 将 OneBot 事件转为 Yuni 内部事件
        SpringYuniEvent springYuniEvent = convertToYuniEvent(oneBotEvent);
        springYuniEvent.setRawJson(oneBotEvent.getRawJson());
        // 借助 Spring 的 EventListener 机制发布事件
        springPublisher.publishEvent(springYuniEvent);
    }

    private SpringYuniEvent convertToYuniEvent(OneBotEvent event) {
        switch (event.getPostType()) {
            case OneBotPostType.MESSAGE:
                return buildYuniMessageEvent(event);
            case OneBotPostType.META_EVENT:
                return buildYuniMetaEvent(event);
            case OneBotPostType.NOTICE:
                return buildYuniNoticeEvent(event);
            case OneBotPostType.REQUEST:
                return buildYuniRequestEvent(event);
            default:
                log.info("不支持的 OneBot 事件类型：{}", event.getPostType());
        }
        return new SpringYuniEvent() {
            @Override
            public String toLogString() {
                return "";
            }
        };
    }

    private YuniMessageEvent buildYuniMessageEvent(OneBotEvent event) {
        MessageEvent messageEvent = (MessageEvent) event;
        YuniMessageEvent yuniMessageEvent = BeanCopyUtils.copyBean(messageEvent, YuniMessageEvent.class);
        yuniMessageEvent.setMessage(messageEvent.getMessage());
        yuniMessageEvent.setMessageChain(new MessageChain(yuniMessageEvent.getMessage()));
        yuniMessageEvent.setSender(messageEvent.getSender());
        yuniMessageEvent.setMessageEvent(messageEvent);
        // 为消息事件添加 chatSession
        ChatSession chatSession = new ChatSession();
        chatSession.setSelfId(messageEvent.getSelfId());
        chatSession.setUserId(messageEvent.getUserId());
        chatSession.setGroupId(messageEvent.getGroupId());
        chatSession.setMessageType(messageEvent.getMessageType());
        chatSession.setAdapter(adapter);
        yuniMessageEvent.setChatSession(chatSession);
        return yuniMessageEvent;
    }

    private YuniNoticeEvent buildYuniNoticeEvent(OneBotEvent event) {
        NoticeEvent noticeEvent = (NoticeEvent) event;
        YuniNoticeEvent yuniNoticeEvent = new YuniNoticeEvent();
        yuniNoticeEvent.setRawNoticeEvent(noticeEvent);
        return yuniNoticeEvent;
    }

    private YuniRequestEvent buildYuniRequestEvent(OneBotEvent event) {
        RequestEvent requestEvent = (RequestEvent) event;
        YuniRequestEvent yuniRequestEvent = new YuniRequestEvent();
        yuniRequestEvent.setRawRequestEvent(requestEvent);
        return yuniRequestEvent;
    }

    private YuniMetaEvent buildYuniMetaEvent(OneBotEvent event) {
        MetaEvent metaEvent = (MetaEvent) event;
        YuniMetaEvent yuniMetaEvent = new YuniMetaEvent();
        yuniMetaEvent.setRawMetaEvent(metaEvent);
        return yuniMetaEvent;
    }
}
