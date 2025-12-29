package com.yuier.yuni.event.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title: EventManager
 * @Author yuier
 * @Package com.yuier.yuni.event.init
 * @Date 2025/12/29 19:33
 * @description:
 */

@Component
public class EventManager {

    @Autowired
    EventMapper eventMapper;

    public void init() {
        eventMapper.init();
    }
}
