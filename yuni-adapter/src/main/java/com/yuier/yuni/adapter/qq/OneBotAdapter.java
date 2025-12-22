package com.yuier.yuni.adapter.qq;

import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageChain;

/**
 * @Title: OneBotAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq
 * @Date 2025/12/22 3:50
 * @description: QQ 协议适配器接口
 */

public interface OneBotAdapter {

    void startListening();
    void stopListening();
    OneBotEvent handleReportJson(String json);
    void sendGroupMessage(long groupId, MessageChain message);
    void sendPrivateMessage(long userId, MessageChain message);
}
