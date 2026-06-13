package com.yuier.yuni.webapi.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * WebSocket 推送消息信封。
 */
@Data
@AllArgsConstructor
public class WsMessage<T> {
    /** 消息类型，如 new_message */
    private String type;
    /** 业务数据 */
    private T data;
}
