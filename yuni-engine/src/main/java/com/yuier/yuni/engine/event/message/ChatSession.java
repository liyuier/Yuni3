package com.yuier.yuni.engine.event.message;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.core.model.message.MessageChain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public ChatSession(OneBotAdapter adapter) {
        this.adapter = adapter;
    }

    public void response(MessageChain chain) {
        if (messageType.equals(MessageType.PRIVATE)) {
            adapter.sendPrivateMessage(selfId, chain);
        } else if (messageType.equals(MessageType.GROUP)) {
            adapter.sendGroupMessage(groupId, chain);
        }
    }
}
