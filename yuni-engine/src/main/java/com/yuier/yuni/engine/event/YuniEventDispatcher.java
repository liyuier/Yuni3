package com.yuier.yuni.engine.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotHttpAdapter;
import com.yuier.yuni.adapter.qq.http.OneBotResponse;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.plugin.manage.PluginManager;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class YuniEventDispatcher {

    @Autowired
    OneBotAdapter adapter;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    PluginManager pluginManager;

    @EventListener
    public void messageEventHandler(YuniMessageEvent event) {
        log.info(event.toLogString());
        pluginManager.handleMessageEvent(event);
    }
}
