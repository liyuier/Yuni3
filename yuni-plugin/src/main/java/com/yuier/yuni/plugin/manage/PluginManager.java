package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.util.LogStringUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.YuniMessageSentEvent;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
import com.yuier.yuni.plugin.manage.enable.PluginEnableProcessor;
import com.yuier.yuni.plugin.manage.load.PluginLoadProcessor;
import com.yuier.yuni.plugin.manage.match.PassivePluginMatcher;
import com.yuier.yuni.plugin.manage.register.PluginRegisterProcessor;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.Data;
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

@Data
@Component
@Slf4j
public class PluginManager {

    // TODO 理顺逻辑，解决一堆管理功能

    @Value("${bot.app.plugin.directory:yuni-application/plugins}")
    private String pluginDirectoryPath;  // 插件目录
    @Autowired
    PluginContainer pluginContainer;
    @Autowired
    PluginEnableProcessor pluginEnableProcessor;
    @Autowired
    PluginLoadProcessor pluginLoadProcessor;
    @Autowired
    PluginRegisterProcessor pluginRegisterProcessor;
    @Autowired
    PassivePluginMatcher passivePluginMatcher;

    /**
     * 初始化插件系统
     */
    public void initializePlugins() {
        // 获取插件 jar 包
        List<File> files = pluginLoadProcessor.collectJarFilesFromPath(pluginDirectoryPath);
        for (File jarFile : files) {
            loadAndRegisterPluginsFromSingleJarFile(jarFile);
        }
    }

    public void loadAndRegisterPluginsFromSingleJarFile(File jarFile) {
        log.info("扫描 jar 包: {}", LogStringUtil.buildYellowLog(jarFile.getName()));
        try {
            // 先装配插件模块实例
            PluginModuleInstance pluginModuleInstance = pluginLoadProcessor.assemblePluginModule(jarFile);
            registerPluginModuleInstance(pluginModuleInstance);

            // 再解析插件实例列表
            List<Class<?>> pluginClasses = pluginLoadProcessor.loadPluginClassesFromJarFile(jarFile);
            List<PluginInstance> pluginInstanceList = pluginLoadProcessor.assemblePluginInstances(pluginModuleInstance, pluginClasses);
            registerPluginInstances(pluginInstanceList);
        } catch (Exception e) {
            log.error("加载 jar 包失败: {}", jarFile.getName(), e);
        }
        log.info("{} 加载完毕", LogStringUtil.buildYellowLog(jarFile.getName()));
    }

    /**
     * 注册新插件模块实例
     * @param instance 新插件模块实例
     */
    public void registerPluginModuleInstance(PluginModuleInstance instance) {
        pluginRegisterProcessor.registerNewPluginModuleInstance(instance);
    }

    /**
     * 注册插件实例
     * @param instances 插件实例列表
     */
    public void registerPluginInstances(List<PluginInstance> instances) {
        pluginRegisterProcessor.registerPluginInstances(instances);
    }

    /**
     * 根据插件 id 获取插件实例
     * @param pluginId 插件 id
     * @return 插件实例
     */
    public PluginInstance getPluginInstanceById(String pluginId) {
        return pluginContainer.getPluginInstanceByFullId(pluginId);
    }

    /**
     * 处理消息事件
     * @param event  消息事件
     */
    public void handleMessageEvent(YuniMessageEvent event) {
        passivePluginMatcher.matchMessageEvent(event);
    }

    // 处理 notice 事件
    public void handleNoticeEvent(YuniNoticeEvent event) {
        passivePluginMatcher.matchNoticeEvent(event);
    }

    // 处理 request 事件
    public void handleRequestEvent(YuniRequestEvent event) {
        passivePluginMatcher.matchRequestEvent(event);
    }

    // 处理 meta 事件
    public void handleMetaEvent(YuniMetaEvent event) {
        passivePluginMatcher.matchMetaEvent(event);
    }

    /**
     * 插件开关控制
     */
    public void enablePlugin(YuniMessageEvent eventContext, String pluginId) {
        pluginEnableProcessor.enablePlugin(eventContext, pluginId);
    }

    public void disablePlugin(YuniMessageEvent eventContext, String pluginId) {
        pluginEnableProcessor.disablePlugin(eventContext, pluginId);
    }

    /**
     * 插件卸载
     */
    public void unloadPlugin(String pluginId) {
        PluginInstance pluginInstance = pluginContainer.getPluginInstanceByFullId(pluginId);
        switch (pluginInstance) {
            case null -> log.error("插件 {} 不存在", pluginId);
            case ActivePluginInstance activePluginInstance ->
                    // 移除主动插件
                    removeActivePlugin(pluginId);
            case PassivePluginInstance passivePluginInstance ->
                    // 移除被动插件
                    removePassivePlugin(pluginId);
            default -> {
            }
        }

    }

    public void removeActivePlugin(String pluginId) {
        // 移除主动插件的定时任务
    }

    public void removePassivePlugin(String pluginId) {
        // 移除被动插件

    }

    public void handleMessageSentEvent(YuniMessageSentEvent event) {
        passivePluginMatcher.matchMessageSentEvent(event);
    }
}

