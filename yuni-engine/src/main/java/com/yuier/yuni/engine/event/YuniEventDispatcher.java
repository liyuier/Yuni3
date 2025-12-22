package com.yuier.yuni.engine.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotHttpAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotResponse;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Title: YuniEventDispatcher
 * @Author yuier
 * @Package com.yuier.yuni.engine.event
 * @Date 2025/12/22 17:22
 * @description: 事件分发器
 */

@Component
public class YuniEventDispatcher {

    @Autowired
    OneBotAdapter adapter;
    @Autowired
    ObjectMapper mapper;

    @EventListener
    public void messageEventHandler(YuniMessageEvent event) throws JsonProcessingException {
        System.out.println(event.getMessageChain().toString());
        System.out.println("处理消息事件中，，，");
        System.out.println("尝试获取消息 ID 280949789");
        MessageEvent response = adapter.getMsg(280949789L);
        System.out.println("获取群消息成功");
    }
}
