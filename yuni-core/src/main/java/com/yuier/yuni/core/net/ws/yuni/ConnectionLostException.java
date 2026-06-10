package com.yuier.yuni.core.net.ws.yuni;

/**
 * @Title: ConnectionLostException
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2026/06/10
 * @description: WebSocket 连接断开时抛出，携带所有等待中的请求将立即失败，
 *               而非等待超时。上层可捕获此异常实现重试。
 */
public class ConnectionLostException extends RuntimeException {

    public ConnectionLostException(String message) {
        super(message);
    }

    public ConnectionLostException(String message, Throwable cause) {
        super(message, cause);
    }
}
