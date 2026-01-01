package com.yuier.yuni.engine.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
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

    @EventListener
    public void noticeEventHandler(YuniNoticeEvent event) {
        pluginManager.handleNoticeEvent(event);
    }

    @EventListener
    public void requestEventHandler(YuniRequestEvent event) {
        log.info(event.toLogString());
        pluginManager.handleRequestEvent(event);
    }

    @EventListener
    public void metaEventHandler(YuniMetaEvent event) {
        pluginManager.handleMetaEvent(event);
    }
}
