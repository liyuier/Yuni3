package com.yuier.yuni.event.model.context;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
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
 * @Package com.yuier.yuni.core.model.message.session
 * @Date 2025/12/22 17:24
 * @description: 会话
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    // 收到消息的机器人 QQ 号
    private Long selfId;

    // 发送消息的用户
    private Long userId;

    // 发送消息的群组
    private Long groupId;

    // 当前是否处于持续对话状态
    private boolean longSession;

    // 当前消息是群消息还是私聊消息
    private MessageType messageType;

    // 当前全局适配器
    private OneBotAdapter adapter;

    // 当前会话的 OneBot 侧基础 URL
    private String OneBotBaseUrl;

    public ChatSession(OneBotAdapter adapter) {
        this.adapter = adapter;
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

    public void response(MessageChain chain) {
        if (messageType.equals(MessageType.PRIVATE)) {
            adapter.sendPrivateMessage(selfId, chain);
        } else if (messageType.equals(MessageType.GROUP)) {
            adapter.sendGroupMessage(groupId, chain);
        }
    }

    public void response(String text) {
        response(new MessageChain(text));
    }
}
