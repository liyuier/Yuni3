package com.yuier.yuni.core.net.ws.example;

import com.yuier.yuni.core.net.ws.WsResponseWrapper;

/**
 * @Title: DefaultWsResponseWrapper
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.example
 * @Date 2025/12/25 16:07
 * @description: 默认 ws 响应包装器
 */

public class DefaultWsResponseWrapper implements WsResponseWrapper {

    @Override
    public String wrapRawJson(String rawJson) {
        return rawJson;
    }
}
