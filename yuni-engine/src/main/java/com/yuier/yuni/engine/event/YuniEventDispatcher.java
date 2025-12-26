package com.yuier.yuni.engine.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.persistence.YuniEventSaver;
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
    @Autowired
    YuniEventSaver eventSaver;

    @EventListener
    public void messageEventHandler(YuniMessageEvent event) {
        log.info(event.toLogString());
        eventSaver.saveEvent(event);
        pluginManager.handleMessageEvent(event);
    }
}
