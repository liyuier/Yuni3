package com.yuier.yuni.adapter.onebot.transport.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: OneBotHttpEventController
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot.transport.http
 * @Date 2026/06/09
 * @description: OneBot HTTP POST 事件接收 Controller。
 *               仅在 yuni.protocol.type = onebot 且 mode = http 时注册。
 */

@RestController
@ConditionalOnProperty(name = "yuni.protocol.onebot.mode", havingValue = "http", matchIfMissing = true)
public class OneBotHttpEventController {

    private final OneBotHttpTransport transport;

    public OneBotHttpEventController(OneBotHttpTransport transport) {
        this.transport = transport;
    }

    @PostMapping("/")
    public void receiveOneBotHttpPost(@RequestBody String rawJson) {
        transport.receiveEvent(rawJson);
    }
}
