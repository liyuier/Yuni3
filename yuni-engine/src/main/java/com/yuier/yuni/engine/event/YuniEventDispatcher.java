package com.yuier.yuni.engine.event;

import com.yuier.yuni.engine.event.model.YuniMessageEvent;
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

    @EventListener
    public void messageEventHandler(YuniMessageEvent event) {
        System.out.println("处理消息事件中，，，");
    }
}
