package com.yuier.yuni.adapter.qq.http;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.util.OneBotDeserializer;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void handleReportJson(String json) {
        System.out.println("OneBot HTTP POST adapter ...");
        try {
            OneBotEvent oneBotEvent = deserializer.deserializeEvent(json);
            System.out.println("deserializer succeed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendGroupMessage(long groupId, MessageEvent message) {

    }

    @Override
    public void sendPrivateMessage(long userId, MessageEvent message) {

    }

}
