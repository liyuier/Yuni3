package com.yuier.yuni.plugin.init;

import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

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

    // TODO 理顺逻辑

    private final PluginInstanceAssembler pluginInstanceAssembler;

    @Autowired
    PluginManager pluginManager;

    @Value("${bot.app.plugin.directory:yuni-application/plugins}")
    private String pluginDirectoryPath;  // 插件目录

    public PluginInitializationRunner(PluginInstanceAssembler pluginInstanceAssembler) {
        this.pluginInstanceAssembler = pluginInstanceAssembler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化插件系统...");
        initializePlugins();
    }

    private void initializePlugins() throws Exception {
        File pluginDir = new File(pluginDirectoryPath);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", pluginDirectoryPath);
            return;
        }

        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            log.warn("插件目录为空: {}", pluginDirectoryPath);
            return;
        }

        for (File jarFile : jarFiles) {
            log.info("正在加载插件: {}", jarFile.getName());
            try {
                List<PluginInstance> instances = pluginInstanceAssembler.assembleFromJar(jarFile);
                registerPluginInstances(instances);
            } catch (Exception e) {
                log.error("加载插件失败: {}", jarFile.getName(), e);
            }
        }
    }

    private void registerPluginInstances(List<PluginInstance> instances) {
        // 根据插件类型注册到对应的系统中
        for (PluginInstance instance : instances) {
            if (instance instanceof ScheduledPluginInstance) {
                // 注册主动插件到定时任务系统
                registerActivePlugin((ScheduledPluginInstance) instance);
            } else if (instance instanceof PassivePluginInstance) {
                // 注册被动插件到事件监听系统
                registerPassivePlugin((PassivePluginInstance) instance);
            }
        }
    }

    private void registerActivePlugin(ScheduledPluginInstance instance) {
        // 实现主动插件的注册逻辑
        log.info("注册主动插件: {}", instance.getPluginMetadata().getName());
        pluginManager.registerActivePlugin(instance);
    }

    private void registerPassivePlugin(PassivePluginInstance instance) {
        // 实现被动插件的注册逻辑
        log.info("注册被动插件: {}", instance.getPluginMetadata().getName());
        pluginManager.registerPassivePlugin(instance);
    }
}

