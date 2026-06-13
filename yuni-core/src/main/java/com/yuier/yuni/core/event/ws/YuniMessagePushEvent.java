package com.yuier.yuni.core.event.ws;

import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.YuniMessageSentEvent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息推送事件。
 * 由 YuniEventDispatcher 发布，WebSocket 推送服务监听消费。
 */
@Getter
public class YuniMessagePushEvent extends ApplicationEvent {

    private final Long groupId;
    private final String senderName;
    private final String rawMessage;
    private final boolean plainText;
    private final String rawJson;
    private final boolean selfSent;

    public YuniMessagePushEvent(Object source, Long groupId, String senderName,
                                String rawMessage, boolean plainText, String rawJson,
                                boolean selfSent) {
        super(source);
        this.groupId = groupId;
        this.senderName = senderName;
        this.rawMessage = rawMessage;
        this.plainText = plainText;
        this.rawJson = rawJson;
        this.selfSent = selfSent;
    }

    public static YuniMessagePushEvent from(YuniMessageEvent event) {
        return new YuniMessagePushEvent(
                event, event.getGroupId(),
                event.getSender().getNickname(),
                event.getRawMessage(),
                event.getMessageChain().isPlainText(),
                event.getRawJson(), false);
    }

    public static YuniMessagePushEvent from(YuniMessageSentEvent event) {
        return new YuniMessagePushEvent(
                event, event.getGroupId(),
                event.getSender().getNickname(),
                event.getRawMessage(),
                event.getMessageChain().isPlainText(),
                event.getRawJson(), true);
    }
}
