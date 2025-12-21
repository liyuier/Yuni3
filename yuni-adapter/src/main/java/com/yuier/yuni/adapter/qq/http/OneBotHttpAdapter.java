package com.yuier.yuni.adapter.qq.http;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.message.QqMessage;

/**
 * @Title: OneBotHttpAdapter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.http
 * @Date 2025/12/22 3:57
 * @description: OneBot HTTP 通信方式
 */

public class OneBotHttpAdapter implements OneBotAdapter {
    @Override
    public void startListening() {

    }

    @Override
    public void stopListening() {

    }

    @Override
    public void handleReportJson(String json) {
        System.out.println("OneBot HTTP POST adapter ...");
    }

    @Override
    public void sendGroupMessage(long groupId, QqMessage message) {

    }

    @Override
    public void sendPrivateMessage(long userId, QqMessage message) {

    }

}
