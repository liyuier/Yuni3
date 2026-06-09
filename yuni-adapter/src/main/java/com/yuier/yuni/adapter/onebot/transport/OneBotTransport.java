package com.yuier.yuni.adapter.onebot.transport;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Title: OneBotTransport
 * @Author yuier
 * @Package com.yuier.yuni.adapter.onebot.transport
 * @Date 2026/06/09
 * @description: OneBot 传输层接口。
 *               定义与 OneBot 实现通信的传输抽象，
 *               支持 HTTP 和 WebSocket 两种实现方式。
 */

public interface OneBotTransport {

    /**
     * 启动传输层连接
     * @return 连接完成的 Future
     */
    CompletableFuture<Void> connect();

    /**
     * 断开传输层连接
     * @return 断开完成的 Future
     */
    CompletableFuture<Void> disconnect();

    /**
     * 发送 API 请求并等待响应
     * @param action OneBot API 动作名
     * @param params 请求参数
     * @return 响应 data 字段的 JSON 字符串
     */
    String sendApiRequest(String action, Map<String, Object> params);

    /**
     * 注册事件回调（传输层收到事件推送时调用）
     * @param callback 事件回调，接收原始 JSON 字符串
     */
    void setEventCallback(java.util.function.Consumer<String> callback);

    /**
     * 是否已连接
     */
    boolean isConnected();
}
