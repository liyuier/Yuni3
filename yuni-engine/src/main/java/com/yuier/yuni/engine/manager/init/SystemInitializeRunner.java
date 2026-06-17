package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.contact.manage.YuniContactManager;
import com.yuier.yuni.event.init.EventManager;
import com.yuier.yuni.permission.manage.UserPermissionManager;
import com.yuier.yuni.plugin.manage.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Title: SystemInitializeRunner
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/24 15:15
 * @description: 系统启动初始化进程
 */

@Component
@Slf4j
public class SystemInitializeRunner implements ApplicationRunner {

    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private SystemInitializeProcessor systemInitializeProcessor;
    @Autowired
    private UserPermissionManager userPermissionManager;
    @Autowired
    private EventManager eventManager;
    @Autowired
    private BotManager botManager;
    @Autowired
    private YuniContactManager yuniContactManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("检查数据库文件...");
        systemInitializeProcessor.checkDatabaseFile();
        log.info("初始化权限系统...");
        userPermissionManager.initUserPermission();
        log.info("初始化事件系统...");
        eventManager.init();
        log.info("拉起适配器...");
        botManager.startBot();
        log.info("初始化联系人系统...");
        yuniContactManager.init();
        log.info("初始化插件系统...");
        pluginManager.initializePlugins();
        log.info("""
                Yuni 启动完毕 Ciallo～(∠・ω< )⌒★!""");
    }
}
