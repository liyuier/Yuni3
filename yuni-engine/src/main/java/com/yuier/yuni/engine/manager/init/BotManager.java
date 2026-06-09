package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.core.bot.BotStatus;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.engine.event.EventBridge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Title: BotManager
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2026/06/09
 * @description: Bot 连接管理器。
 *               负责在系统启动时注册业务回调、建立适配器连接。
 *               将 bot 初始化的细节从 SystemInitializeRunner 中抽离。
 */

@Slf4j
@Component
public class BotManager {

    @Autowired
    private YuniBot bot;
    @Autowired
    private EventBridge eventBridge;

    /**
     * 初始化 bot 连接：
     * 1. 注册业务层事件回调
     * 2. 建立与聊天前端的连接
     */
    public void startBot() {
        log.info("注册业务事件回调...");
        bot.setEventCallback(eventBridge);

        log.info("建立与聊天前端的连接...");
        bot.connect().thenRun(() -> {
            if (bot.getStatus() == BotStatus.ONLINE) {
                log.info("Bot [{}] 已成功连接", bot.getBotId());
            } else {
                log.warn("Bot [{}] 连接状态: {}", bot.getBotId(), bot.getStatus());
            }
        }).exceptionally(e -> {
            log.error("Bot 连接失败: {}", e.getMessage());
            return null;
        });
    }
}
