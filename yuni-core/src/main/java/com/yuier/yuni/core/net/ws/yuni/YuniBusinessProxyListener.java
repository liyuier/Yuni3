package com.yuier.yuni.core.net.ws.yuni;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Title: YuniBusinessProxyListener
 * @Author yuier
 * @Package com.yuier.yuni.core.net.ws.yuni
 * @Date 2025/12/31 7:21
 * @description: ws listener 代理
 */

public interface YuniBusinessProxyListener {
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason);

    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason);

    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response);

    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text);

    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes);

    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response);
}
