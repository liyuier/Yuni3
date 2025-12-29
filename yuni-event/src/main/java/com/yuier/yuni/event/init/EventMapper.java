package com.yuier.yuni.event.init;

import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.meta.HeartbeatEvent;
import com.yuier.yuni.core.model.meta.LifecycleEvent;
import com.yuier.yuni.event.context.SpringYuniEvent;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.meta.YuniHeartbeatEvent;
import com.yuier.yuni.event.context.meta.YuniLifecycleEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: EventMapper
 * @Author yuier
 * @Package com.yuier.yuni.engine.event
 * @Date 2025/12/29 19:23
 * @description:
 */

@Component
public class EventMapper {

    private Map<Class<? extends OneBotEvent>, Class<? extends SpringYuniEvent>> eventMap;

    public void init() {
        eventMap = new ConcurrentHashMap<>() {{
           put(MessageEvent.class, YuniMessageEvent.class);
            put(HeartbeatEvent.class, YuniHeartbeatEvent.class);
            put(LifecycleEvent.class, YuniLifecycleEvent.class);
        }};
    }

    public Class<? extends SpringYuniEvent> mapToYuniEvent(Class<? extends OneBotEvent> eventClass) {
        return eventMap.get(eventClass);
    }

}
