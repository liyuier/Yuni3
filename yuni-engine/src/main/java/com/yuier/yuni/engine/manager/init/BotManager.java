package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.core.bot.BotStatus;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.bot.YuniBotFactory;
import com.yuier.yuni.engine.event.EventBridge;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Title: BotManager
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2026/06/09
 * @description: Bot 管理器。
 *               在启动时根据 yuni.protocol.type 从所有 YuniBotFactory 中选择匹配的工厂、
 *               创建 YuniBot 实例、注册业务回调、建立连接。
 *               适配器模块只需提供 YuniBotFactory 实现即可被自动发现。
 */

@Slf4j
@Component
public class BotManager {

    @Value("${yuni.protocol.type:}")
    private String protocolType;

    @Autowired
    private List<YuniBotFactory> botFactories;
    @Autowired
    private EventBridge eventBridge;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Getter
    private YuniBot bot;

    /**
     * 初始化 bot 连接：
     * 1. 根据配置发现匹配的 YuniBotFactory
     * 2. 调用工厂创建 YuniBot 实例
     * 3. 将创建的 bot 注册为 Spring Bean，供其他组件注入
     * 4. 注册业务层事件回调
     * 5. 建立与聊天前端的连接
     */
    public void startBot() {
        if (protocolType == null || protocolType.isBlank()) {
            log.warn("未配置 yuni.protocol.type，跳过 bot 连接初始化");
            return;
        }

        YuniBotFactory factory = botFactories.stream()
                .filter(f -> protocolType.equals(f.getProtocolType()))
                .findFirst()
                .orElse(null);

        if (factory == null) {
            log.error("未找到匹配的 YuniBotFactory: protocolType={}, 可用工厂: {}",
                    protocolType, botFactories.stream().map(YuniBotFactory::getProtocolType).toList());
            return;
        }

        log.info("使用 {} 工厂创建 bot...", factory.getProtocolType());
        bot = factory.createBot();

        // 将 bot 注册为 Spring Bean，供 PluginUtils 等通过 SpringContextUtil 获取
        DefaultSingletonBeanRegistry registry =
                (DefaultSingletonBeanRegistry) applicationContext.getBeanFactory();
        registry.registerSingleton("yuniBot", bot);

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

    /** 获取当前 bot 实例，供其他组件通过 BotManager 访问 */
    public YuniBot getBot() {
        if (bot == null) {
            throw new IllegalStateException("Bot 尚未初始化，请检查 yuni.protocol.type 配置");
        }
        return bot;
    }
}
