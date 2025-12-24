package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Title: PluginRegisterProcessor
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/24 16:46
 * @description: 插件注册管理
 */

@Component
@Slf4j
public class PluginRegisterProcessor {


    @Autowired
    private DynamicTaskManager dynamicTaskManager;

    public void registerPluginInstances(List<PluginInstance> instances, PluginContainer pluginContainer) {
        // 根据插件类型注册到对应的系统中
        for (PluginInstance instance : instances) {
            if (instance instanceof ScheduledPluginInstance) {
                // 注册主动插件到定时任务系统
                registerActivePlugin((ScheduledPluginInstance) instance, pluginContainer);
            } else if (instance instanceof PassivePluginInstance) {
                // 注册被动插件到事件监听系统
                registerPassivePlugin((PassivePluginInstance) instance, pluginContainer);
            }
        }
    }

    /**
     * 注册主动插件
     * @param instance 主动插件实例
     */
    private void registerActivePlugin(ScheduledPluginInstance instance, PluginContainer pluginContainer) {
        log.info("正在注册主动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        String pluginId = instance.getPluginMetadata().getId();
        pluginContainer.getActivePlugins().put(pluginId, instance);

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
    private void registerPassivePlugin(PassivePluginInstance instance, PluginContainer pluginContainer) {
        log.info("正在注册被动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        String pluginId = instance.getPluginMetadata().getId();
        pluginContainer.getPassivePlugins().put(pluginId, instance);
    }

}
