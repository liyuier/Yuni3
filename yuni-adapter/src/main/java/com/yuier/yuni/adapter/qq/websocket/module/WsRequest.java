package com.yuier.yuni.adapter.qq.websocket.module;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Title: WsRequest
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket.module
 * @Date 2025/12/29 0:34
 * @description: ws 请求模型
 */

@Data
@AllArgsConstructor
public class WsRequest {
    private String action;
    private Map<String, Object> params;
    private String echo;

    public WsRequest(String action, Map<String, Object> params) {
        this.action = action;
        this.params = params;
        this.echo = UUID.randomUUID().toString();
    }

    public WsRequest(String action) {
        this.action = action;
        this.params = new HashMap<>();
        this.echo = UUID.randomUUID().toString();
    }

}