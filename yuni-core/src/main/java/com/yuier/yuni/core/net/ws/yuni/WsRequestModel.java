package com.yuier.yuni.core.net.ws.yuni;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * @Title: WsRequestModel
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2026/1/2 3:27
 * @description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsRequestModel {

    CompletableFuture<String> future;
    String echoFlag;
    String message;
}
