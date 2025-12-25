package com.yuier.yuni.core.net.ws;

/**
 * @Title: WsResponseWrapper
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws
 * @Date 2025/12/25 16:03
 * @description: ws 响应包装类接口
 */

public interface WsResponseWrapper {

    String wrapRawJson(String rawJson);
}
