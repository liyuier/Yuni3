package com.yuier.yuni.engine.event;

import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.YuniMessageSentEvent;
import com.yuier.yuni.core.event.meta.YuniMetaEvent;
import com.yuier.yuni.core.event.notice.YuniNoticeEvent;
import com.yuier.yuni.core.event.request.YuniRequestEvent;
import com.yuier.yuni.event.persistence.YuniEventSaver;
import com.yuier.yuni.event.util.EventLogUtil;
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
    PluginManager pluginManager;
    @Autowired
    YuniEventSaver eventSaver;

    @EventListener
    public void messageEventHandler(YuniMessageEvent event) {
        log.info(EventLogUtil.toLog(event));
        eventSaver.saveEvent(event);
        pluginManager.handleMessageEvent(event);
    }

    @EventListener
    public void messageSentEventHandler(YuniMessageSentEvent event) {
        log.info(EventLogUtil.toLog(event));
        eventSaver.saveEvent(event);
        pluginManager.handleMessageSentEvent(event);
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
