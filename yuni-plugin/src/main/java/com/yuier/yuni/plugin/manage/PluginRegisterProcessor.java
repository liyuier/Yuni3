package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.event.message.detector.YuniEventDetector;
import com.yuier.yuni.event.message.detector.command.CommandDetector;
import com.yuier.yuni.event.message.detector.pattern.PatternDetector;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<YuniPlugin, String> pluginBeanToIdMap = new HashMap<>();

    public void registerPluginModuleInstance(PluginModuleInstance instance, PluginContainer pluginContainer) {
        pluginContainer.getPluginModules().put(instance.getPluginModuleMetadata().getModuleId(), instance);
        registerPluginInstances(instance.getPluginInstances(), pluginContainer);
    }

    public void registerPluginInstances(List<PluginInstance> instances, PluginContainer pluginContainer) {
        // 在入口处统一维护插件示例-id映射
        for (PluginInstance instance : instances) {
            mapPluginBeanToId(instance);
        }

        // 根据插件类型注册
        for (PluginInstance instance : instances) {
            if (instance instanceof ActivePluginInstance) {
                // 注册定时插件
                registerActivePlugin((ActivePluginInstance) instance, pluginContainer);
            } else if (instance instanceof PassivePluginInstance) {
                // 注册被动插件
                registerPassivePlugin((PassivePluginInstance) instance, pluginContainer);
            }
        }
    }

    public String mapToPluginId(YuniPlugin bean) {
        return pluginBeanToIdMap.get(bean);
    }

    private void mapPluginBeanToId(PluginInstance instance) {
        pluginBeanToIdMap.put(instance.getPlugin(), instance.getPluginMetadata().getId());
    }

    /**
     * 注册定时插件
     * @param instance 定时插件实例
     */
    private void registerActivePlugin(ActivePluginInstance instance, PluginContainer pluginContainer) {

        // 先把插件保存起来
        String pluginId = instance.getPluginMetadata().getId();
        pluginContainer.getActivePlugins().put(pluginId, instance);
        log.info("正在注册主动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        // 判断是否默认开启
        Boolean defaultEnable = instance.getPluginMetadata().getDefaultEnable();
        if (!defaultEnable) {
            log.info("主动插件 {} 默认不生效，已经跳过", instance.getPluginMetadata().getName());
            return;
        }

        // 判断是定时任务还是即时任务
        if (instance instanceof ScheduledPluginInstance) {
            registerSchedulePlugin((ScheduledPluginInstance) instance, pluginContainer);
        } else if (instance instanceof ImmediatePluginInstance) {
            registerImmediatePlugin((ImmediatePluginInstance) instance, pluginContainer);
        }
    }

    // 注册即时任务
    private void registerImmediatePlugin(ImmediatePluginInstance instance, PluginContainer pluginContainer) {
        instance.getAction().execute();
        log.info("即时插件 {} 已执行完毕", instance.getPluginMetadata().getName());
    }

    /**
     * 注册定时任务
     * @param instance 定时任务实例
     */
    private void registerSchedulePlugin(ScheduledPluginInstance instance, PluginContainer pluginContainer) {
        String pluginId = instance.getPluginMetadata().getId();
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
        /* TODO 重构 */
        log.info("正在注册被动插件: {} | {}", instance.getPluginMetadata().getName(), instance.getPluginMetadata().getId());
        YuniEventDetector<?> detector = instance.getDetector();
        String pluginId = instance.getPluginMetadata().getId();
        if (detector instanceof CommandDetector) {
            pluginContainer.getCommandPlugins().put(pluginId, instance);
        } else if (detector instanceof PatternDetector) {
            pluginContainer.getPatternPlugins().put(pluginId, instance);
        }
    }

}
