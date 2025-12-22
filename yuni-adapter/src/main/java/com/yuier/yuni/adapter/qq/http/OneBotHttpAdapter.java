package com.yuier.yuni.adapter.qq.http;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.model.message.MessageChain;

/**
 * @Title: OneBotHttpAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.http
 * @Date 2025/12/22 3:57
 * @description: OneBot HTTP 通信方式
 */

public class OneBotHttpAdapter implements OneBotAdapter {

    private OneBotDeserializer deserializer;

    public OneBotHttpAdapter(OneBotDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public void startListening() {

    }

    @Override
    public void stopListening() {

    }

    @Override
    public OneBotEvent handleReportJson(String json) {
        try {
            return deserializer.deserializeEvent(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendGroupMessage(long groupId, MessageChain message) {

    }

    @Override
    public void sendPrivateMessage(long userId, MessageChain message) {

    }

}
