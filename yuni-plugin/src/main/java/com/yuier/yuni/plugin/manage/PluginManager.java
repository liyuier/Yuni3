package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.event.model.context.SpringYuniEvent;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.MessageDetector;
import com.yuier.yuni.event.model.message.detector.YuniEventDetector;
import com.yuier.yuni.plugin.init.PluginInstanceAssembler;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, ScheduledPluginInstance> activePlugins = new ConcurrentHashMap<>();
    private final Map<String, PassivePluginInstance> passivePlugins = new ConcurrentHashMap<>();
    @Autowired
    private DynamicTaskManager dynamicTaskManager;

    public void initializePlugins() {
        // 加载插件 jar 包
        File[] pluginJars = pluginInstanceAssembler.loadPluginJars(pluginDirectoryPath);
        // 注册插件
        for (File jarFile : pluginJars) {
            log.info("正在加载 jar 包: {}", jarFile.getName());
            try {
                List<PluginInstance> pluginInstances = pluginInstanceAssembler.assembleFromJar(jarFile);
                registerPluginInstances(pluginInstances);
            } catch (Exception e) {
                log.error("加载 jar 包失败: {}", jarFile.getName(), e);
            }
            log.info("{} 加载完成", jarFile.getName());
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

    /**
     * 注册主动插件
     * @param instance 主动插件实例
     */
    public void registerActivePlugin(ScheduledPluginInstance instance) {
        log.info("正在注册主动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        String pluginId = instance.getPluginMetadata().getId();
        activePlugins.put(pluginId, instance);

        // 创建定时任务
        Runnable task = () -> {
            try {
                instance.getAction().execute();
            } catch (Exception e) {
                log.error("执行主动插件失败: {}", pluginId, e);
            }
        };

        // 注册到定时任务系统
        dynamicTaskManager.addCronTask(pluginId, instance.getCronExpression(), task);
    }

    /**
     * 注册被动插件
     * @param instance 被动插件实例
     */
    public void registerPassivePlugin(PassivePluginInstance instance) {
        log.info("正在注册被动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        String pluginId = instance.getPluginMetadata().getId();
        passivePlugins.put(pluginId, instance);
    }

    /**
     * 处理事件
     * @param event
     */
    public void handleMessageEvent(YuniMessageEvent event) {
        for (PassivePluginInstance instance : passivePlugins.values()) {
            // 权限检查
            if (!checkPermission(instance, event)) {
                continue;
            }

            YuniEventDetector<?> detector = instance.getDetector();
            if (!(detector instanceof MessageDetector messageDetector)) {
                continue;
            }

            // 探测器匹配
            if (messageDetector.match(event)) {
                try {
                    // 执行插件
                    instance.getExecuteMethod().invoke(instance.getPassivePlugin(), event);
                } catch (Exception e) {
                    log.error("执行被动插件失败: {}", instance.getPluginMetadata().getId(), e);
                }
            }
        }
    }

    /**
     * 权限检查
     * @param instance 被动插件实例
     * @param event  事件
     * @return 是否通过
     */
    private boolean checkPermission(PassivePluginInstance instance, SpringYuniEvent event) {
        // 获取用户权限（从事件中提取）
        UserPermission userPermission = getUserPermission(event);
        UserPermission requiredPermission = instance.getPermission();

        return userPermission.getPriority() >= requiredPermission.getPriority();
    }

    /**
     * 获取用户权限（需要从事件中提取用户信息）
     * @param event  事件
     * @return 用户权限
     */
    private UserPermission getUserPermission(SpringYuniEvent event) {
        // TODO 实现权限获取逻辑
        return UserPermission.USER; // 默认权限
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
        ScheduledPluginInstance active = activePlugins.remove(pluginId);
        if (active != null) {
            // 取消定时任务
        }

        // 移除被动插件
        passivePlugins.remove(pluginId);
    }
}

