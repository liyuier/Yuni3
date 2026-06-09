package com.yuier.yuni.webapi.controller;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.engine.event.EventBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: OneBotHttpPostController
 * @Author yuier
 * @Package com.yuier.yuni.webapi.controller
 * @Date 2025/12/22 2:06
 * @description: OneBot 采用 HTTP POST 上报方式的接收处
 * @deprecated 已由 adapter 模块中的 OneBotHttpEventController 替代。
 *             新 Controller 直接通过 OneBotHttpTransport 将事件推入 OneBotBot 回调链，
 *             无需经过 engine 的 EventBridge 转发。
 */
@Deprecated
@RestController
@RequestMapping("/")
public class OneBotHttpPostController {

    @Autowired
    OneBotAdapter adapter;
    @Autowired
    EventBridge eventBridge;

    @PostMapping
    public void receiveOneBotHttpPost(@RequestBody String rawJson) {
        OneBotEvent oneBotEvent = adapter.handleReportJson(rawJson);
        oneBotEvent.setRawJson(rawJson);
        eventBridge.publishRawEvent(oneBotEvent);
    }
}
