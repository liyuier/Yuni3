package com.yuier.yuni.adapter.qq.websocket.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: WsRequest
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket.module
 * @Date 2025/12/29 0:35
 * @description: ws 响应模型
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsResponse {
    private String status;
    private Integer retcode;
    private Object data;
    private String message;
    private String wording;
    private String echo;
    private String stream;
}