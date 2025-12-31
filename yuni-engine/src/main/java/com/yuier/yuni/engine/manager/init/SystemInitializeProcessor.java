package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.adapter.config.OneBotCommunicate;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketConnector;
import com.yuier.yuni.core.net.ws.yuni.YuniWebSocketManager;
import com.yuier.yuni.engine.event.EventBridge;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.yuier.yuni.adapter.qq.websocket.OneBotSessionIdConstance.ONEBOT_EVENT_SOCKET_ID;

/**
 * @Title: SystemInitializeProcessor
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/24 15:19
 * @description: 系统初始化
 */

@Component
@Slf4j
public class SystemInitializeProcessor {

    @Value("${bot.app.sqlite-db-file}")
    private String sqliteDbFile;
    @Value("${onebot.communication.mode}")
    private String mode;
    @Autowired
    private OneBotCommunicate config;
    @Autowired
    private YuniWebSocketManager manager;
    @Autowired
    OneBotAdapter adapter;
    @Autowired
    private EventBridge eventBridge;

    public void checkDatabaseFile() throws IOException {
        // 检查数据库文件是否存在
        File dbFile = new File(sqliteDbFile);
        // 创建数据库文件
        if (!dbFile.exists()) {
            log.debug("数据库文件不存在，正在创建...");
            if (dbFile.createNewFile()) {
                log.info("创建数据库文件: " + dbFile.getAbsolutePath());
            } else {
                log.error("未找到数据库文件，且创建失败: " + dbFile.getAbsolutePath());
            }
        }
    }

    public void startOneBotEventSession() {
        if ("ws".equals(mode)) {
            // 建立到 /event 的连接
            Request eventRequest = new Request.Builder()
                    .url(config.getWsUrl() + "/event")
                    .addHeader("Authorization", "Bearer " + config.getToken())
                    .build();
            OneBotEventWsProxyListener eventProxyListener = new OneBotEventWsProxyListener(
                    adapter,
                    eventBridge,
                    config,
                    manager
            );
            YuniWebSocketConnector eventConnector = new YuniWebSocketConnector(eventRequest, eventProxyListener);
            eventConnector.setTimeOutInterval(config.getWsTimeout());
            eventConnector.setHeartBeatInterval(config.getWsHeartbeatInterval());
            manager.startNewConnection(ONEBOT_EVENT_SOCKET_ID, eventConnector);
        }
    }
}
