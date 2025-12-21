package com.yuier.yuni.adapter.qq;

import com.yuier.yuni.core.model.message.QqMessage;

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
    void handleReportJson(String json);
    void sendGroupMessage(long groupId, QqMessage message);
    void sendPrivateMessage(long userId, QqMessage message);
}
