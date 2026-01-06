package com.yuier.yuni.plugin.manage.register;

import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.core.util.LogStringUtil;
import com.yuier.yuni.plugin.manage.NewPluginContainer;
import com.yuier.yuni.plugin.model.NewPluginModuleInstance;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Title: PluginRegisterProcessor
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage.register
 * @Date 2026/1/6 17:50
 * @description:
 */

@Component
@Slf4j
public class PluginRegisterProcessor {

    @Autowired
    DynamicTaskManager dynamicTaskManager;
    @Autowired
    NewPluginContainer pluginContainer;

    /**
     * 注册插件模块实例
     * @param instance 插件模块实例
     */
    public void registerNewPluginModuleInstance(NewPluginModuleInstance instance) {
        pluginContainer.addPluginModule(instance);
    }

    /**
     * 注册插件实例
     * @param instances 插件实例
     */
    public void registerPluginInstances(List<PluginInstance> instances) {
        for (PluginInstance instance : instances) {
            pluginContainer.addPlugin(instance);
            log.info("已注册插件 {} | {} 到系统", LogStringUtil.buildBrightBlueLog(instance.getPluginName()), LogStringUtil.buildBrightBlueLog(instance.getPluginId()));
            // 如果是主动插件，需要特殊处理
            if (instance instanceof ActivePluginInstance) {
                registerActivePlugin((ActivePluginInstance) instance);
            }
        }
    }

    /**
     * 注册主动插件
     * @param instance 主动插件实例
     */
    private void registerActivePlugin(ActivePluginInstance instance) {
        if (!instance.getPluginMetadata().getDefaultEnable()) {
            log.info("主动插件 {} 默认不生效，已跳过执行流程", LogStringUtil.buildBrightBlueLog(instance.getPluginName()));
            return;
        }

        // 判断是定时任务还是即时任务
        if (instance instanceof ScheduledPluginInstance) {
            registerSchedulePlugin((ScheduledPluginInstance) instance);
        } else if (instance instanceof ImmediatePluginInstance) {
            registerImmediatePlugin((ImmediatePluginInstance) instance);
        }
    }

    /**
     * 注册即时插件
     * @param instance 即时插件实例
     */
    private void registerImmediatePlugin(ImmediatePluginInstance instance) {
        CompletableFuture.runAsync(() -> {
            instance.getAction().execute();
            log.info("即时插件 {} 执行完毕", LogStringUtil.buildBrightBlueLog(instance.getPluginName()));
        });
    }

    /**
     * 注册定时插件
     * @param instance 定时插件实例
     */
    private void registerSchedulePlugin(ScheduledPluginInstance instance) {
        String pluginId = instance.getPluginFullId();
        // 创建定时任务
        Runnable task = () -> {
            try {
                instance.getAction().execute();
            } catch (Exception e) {
                log.error("执行主动插件失败: {}", LogStringUtil.buildBrightBlueLog(pluginId), e);
            }
        };

        // 注册到定时任务系统
        dynamicTaskManager.addCronTask(pluginId, instance.getCronExpression(), task);
    }
}
