package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.plugin.init.PluginInstanceAssembler;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @Title: PluginManager
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/23 23:06
 * @description: 插件管理器
 */

@Component
@Slf4j
public class PluginManager {

    // TODO 理顺逻辑，解决一堆管理功能

    @Value("${bot.app.plugin.directory:yuni-application/plugins}")
    private String pluginDirectoryPath;  // 插件目录
    @Autowired
    private PluginInstanceAssembler pluginInstanceAssembler;
    @Autowired
    PluginRegisterProcessor pluginRegisterProcessor;
    @Autowired
    PluginContainer pluginContainer;
    @Autowired
    PassivePluginMatcher passivePluginMatcher;

    /**
     * 初始化插件系统
     */
    public void initializePlugins() {
        // 加载插件 jar 包
        File[] pluginJars = pluginInstanceAssembler.loadPluginJars(pluginDirectoryPath);
        // 注册插件
        for (File jarFile : pluginJars) {
            log.info("正在加载 jar 包: {}", jarFile.getName());
            try {
                // 加载插件
                List<PluginInstance> pluginInstances = pluginInstanceAssembler.assembleFromJar(jarFile);
                // 注册插件
                pluginRegisterProcessor.registerPluginInstances(pluginInstances, pluginContainer);
            } catch (Exception e) {
                log.error("加载 jar 包失败: {}", jarFile.getName(), e);
            }
            log.info("{} 加载完成", jarFile.getName());
        }
    }

    /**
     * 处理消息事件
     * @param event  消息事件
     */
    public void handleMessageEvent(YuniMessageEvent event) {
        passivePluginMatcher.matchMessageEvent(event, pluginContainer);
    }

    /**
     * 插件开关控制
     */
    public void enablePlugin(String pluginId) {
        // 实现插件启用逻辑
    }

    public void disablePlugin(String pluginId) {
        // 实现插件禁用逻辑
    }

    /**
     * 插件卸载
     */
    public void unloadPlugin(String pluginId) {
        // 移除主动插件的定时任务
        removeActivePlugin(pluginId);

        // 移除被动插件
        removePassivePlugin(pluginId);
    }

    public void removeActivePlugin(String pluginId) {
    }

    public void removePassivePlugin(String pluginId) {
        // 移除被动插件

    }
}

