package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.plugin.manage.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Title: PluginInitializationRunner
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 22:31
 * @description: 插件初始化
 */

@Component
@Slf4j
public class PluginInitializationRunner implements ApplicationRunner {

    @Autowired
    PluginManager pluginManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化插件系统...");
        pluginManager.initializePlugins();
    }

}

