package com.yuier.yuni.adapter.qq.websocket;

import com.yuier.yuni.adapter.config.OneBotCommunicate;
import com.yuier.yuni.adapter.qq.websocket.listener.OneBotApiWsListener;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yuier.yuni.adapter.qq.websocket.OneBotSessionIdConstance.ONEBOT_API_SOCKET_ID;

/**
 * @Title: OneBotWsSessionStarter
 * @Author yuier
 * @Package com.yuier.yuni.adapter.qq.websocket
 * @Date 2025/12/29 4:42
 * @description:
 */

@Component
public class OneBotWsSessionStarter {

    @Autowired
    private OneBotCommunicate config;
    @Autowired
    private YuniWebSocketManager manager;
    @Autowired
    OneBotApiWsListener apiListener;


    /**
     * 向 OneBot 实现建立 ws 连接
     */
    public void startOneBotSession() {

        // 建立到 /api 的连接
        OkHttpClient apiClient = new OkHttpClient();
        Request apiRequest = new Request.Builder()
                .url(config.getWsUrl() + "/api")
                .addHeader("Authorization", "Bearer " + config.getToken())
                .build();
        YuniWebSocketConnector apiConnector = new YuniWebSocketConnector(apiClient, apiRequest, apiListener);
        apiConnector.setTimeOutInterval(config.getWsTimeout());
        apiConnector.setHeartBeatInterval(config.getWsHeartbeatInterval());
        apiListener.setConnector(apiConnector);
        manager.startNewConnection(ONEBOT_API_SOCKET_ID, apiConnector);
    }
}
