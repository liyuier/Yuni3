package com.yuier.yuni.adapter.onebot;

import com.yuier.yuni.adapter.onebot.model.*;
import com.yuier.yuni.core.constants.OneBotPostType;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.BeanCopyUtils;
import com.yuier.yuni.core.event.ChatSession;
import com.yuier.yuni.core.event.SpringYuniEvent;
import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.YuniMessageSentEvent;
import com.yuier.yuni.core.event.meta.YuniMetaEvent;
import com.yuier.yuni.core.event.notice.YuniNoticeEvent;
import com.yuier.yuni.core.event.request.YuniRequestEvent;
import com.yuier.yuni.core.bot.YuniBot;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: EventTranslator
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot
 * @Date 2026/06/09
 * @description: OneBot 协议事件 → 应用内部事件翻译器。
 *               在适配器内部完成协议模型到业务模型的转换，
 *               确保传递给业务层的是已装配完成的 SpringYuniEvent。
 */

@Slf4j
public class EventTranslator {

    private final YuniBot bot;

    public EventTranslator(YuniBot bot) {
        this.bot = bot;
    }

    /**
     * 将 OneBot 事件翻译为 SpringYuniEvent
     */
    public SpringYuniEvent translate(OneBotEvent event) {
        SpringYuniEvent yuniEvent;
        switch (event.getPostType()) {
            case OneBotPostType.MESSAGE:
                yuniEvent = buildYuniMessageEvent(event);
                break;
            case OneBotPostType.MESSAGE_SENT:
                yuniEvent = buildYuniMessageSentEvent(event);
                break;
            case OneBotPostType.META_EVENT:
                yuniEvent = buildYuniMetaEvent(event);
                break;
            case OneBotPostType.NOTICE:
                yuniEvent = buildYuniNoticeEvent(event);
                break;
            case OneBotPostType.REQUEST:
                yuniEvent = buildYuniRequestEvent(event);
                break;
            default:
                log.info("不支持的 OneBot 事件类型：{}", event.getPostType());
                yuniEvent = new SpringYuniEvent() {
                    @Override
                    public String toLogString() {
                        return "";
                    }
                };
        }
        yuniEvent.setRawJson(event.getRawJson());
        return yuniEvent;
    }

    private YuniMessageEvent buildYuniMessageEvent(OneBotEvent event) {
        MessageEvent messageEvent = (MessageEvent) event;
        YuniMessageEvent yuniMessageEvent = BeanCopyUtils.copyBean(messageEvent, YuniMessageEvent.class);
        yuniMessageEvent.setMessage(messageEvent.getMessage());
        yuniMessageEvent.setMessageChain(new MessageChain(yuniMessageEvent.getMessage()));
        yuniMessageEvent.setSender(messageEvent.getSender());
        ChatSession chatSession = new ChatSession(bot);
        chatSession.setSelfId(messageEvent.getSelfId());
        chatSession.setUserId(messageEvent.getUserId());
        chatSession.setGroupId(messageEvent.getGroupId());
        chatSession.setMessageType(messageEvent.getMessageType());
        chatSession.setMessageId(messageEvent.getMessageId());
        yuniMessageEvent.setChatSession(chatSession);
        return yuniMessageEvent;
    }

    private YuniMessageSentEvent buildYuniMessageSentEvent(OneBotEvent event) {
        MessageSentEvent messageSentEvent = (MessageSentEvent) event;
        YuniMessageSentEvent yuniMessageSentEvent = BeanCopyUtils.copyBean(messageSentEvent, YuniMessageSentEvent.class);
        yuniMessageSentEvent.setMessage(messageSentEvent.getMessage());
        yuniMessageSentEvent.setMessageChain(new MessageChain(messageSentEvent.getMessage()));
        yuniMessageSentEvent.setSender(messageSentEvent.getSender());
        return yuniMessageSentEvent;
    }

    private YuniNoticeEvent buildYuniNoticeEvent(OneBotEvent event) {
        NoticeEvent noticeEvent = (NoticeEvent) event;
        YuniNoticeEvent yuniNoticeEvent = new YuniNoticeEvent();
        yuniNoticeEvent.setNoticeType(noticeEvent.getNoticeType());
        yuniNoticeEvent.setUserId(noticeEvent.getUserId());
        yuniNoticeEvent.setGroupId(noticeEvent.getGroupId());
        yuniNoticeEvent.setOperatorId(noticeEvent.getOperatorId());
        return yuniNoticeEvent;
    }

    private YuniRequestEvent buildYuniRequestEvent(OneBotEvent event) {
        RequestEvent requestEvent = (RequestEvent) event;
        YuniRequestEvent yuniRequestEvent = new YuniRequestEvent();
        yuniRequestEvent.setRequestType(requestEvent.getRequestType());
        yuniRequestEvent.setUserId(requestEvent.getUserId());
        yuniRequestEvent.setGroupId(requestEvent.getGroupId());
        yuniRequestEvent.setComment(requestEvent.getComment());
        yuniRequestEvent.setFlag(requestEvent.getFlag());
        return yuniRequestEvent;
    }

    private YuniMetaEvent buildYuniMetaEvent(OneBotEvent event) {
        MetaEvent metaEvent = (MetaEvent) event;
        YuniMetaEvent yuniMetaEvent = new YuniMetaEvent();
        yuniMetaEvent.setMetaEventType(metaEvent.getMetaEventType());
        yuniMetaEvent.setSubType(metaEvent.getSubType());
        return yuniMetaEvent;
    }
}
