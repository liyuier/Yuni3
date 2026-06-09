package com.yuier.yuni.event.context;

import com.yuier.yuni.core.bot.MessageSentResult;
import com.yuier.yuni.core.bot.MessageTarget;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.core.model.message.MessageChain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.yuier.yuni.core.constants.OneBotMessageType.GROUP_MESSAGE;
import static com.yuier.yuni.core.constants.OneBotMessageType.PRIVATE_MESSAGE;

/**
 * @Title: ChatSession
 * @Author yuier
 * @Package com.yuier.yuni.event.context
 * @Date 2025/12/22 17:24
 * @description: 会话上下文，提供消息发送、回复等快捷方法
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    private Long selfId;
    private Long userId;
    private Long groupId;
    private boolean longSession;
    private MessageType messageType;
    private YuniBot bot;
    private String OneBotBaseUrl;
    private Long messageId;

    public ChatSession(YuniBot bot) {
        this.bot = bot;
    }

    public void setMessageType(String messageType) {
        if (messageType.equals(PRIVATE_MESSAGE)) {
            this.messageType = MessageType.PRIVATE;
        } else if (messageType.equals(GROUP_MESSAGE)) {
            this.messageType = MessageType.GROUP;
        } else {
            throw new RuntimeException("未知消息类型！");
        }
    }

    /** 发送消息到当前会话 */
    public MessageSentResult response(MessageChain chain) {
        if (messageType.equals(MessageType.PRIVATE)) {
            return bot.sendMessage(MessageTarget.privateChat(userId), chain);
        } else {
            return bot.sendMessage(MessageTarget.group(groupId), chain);
        }
    }

    public MessageSentResult response(String text) {
        return response(new MessageChain(text));
    }

    /** 回复当前消息（自动添加引用） */
    public MessageSentResult reply(MessageChain chain) {
        chain.addReply(String.valueOf(messageId));
        return response(chain);
    }

    public MessageSentResult reply(String text) {
        return reply(new MessageChain(text));
    }
}
