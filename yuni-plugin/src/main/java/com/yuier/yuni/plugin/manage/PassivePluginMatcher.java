package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
import com.yuier.yuni.event.detector.message.command.CommandDetector;
import com.yuier.yuni.event.detector.message.pattern.PatternDetector;
import com.yuier.yuni.permission.manage.UserPermissionManager;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import com.yuier.yuni.plugin.persistence.SavePluginCallEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Title: PassivePluginMatcher
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/24 17:35
 * @description: 插件匹配器
 */

@Component
@Slf4j
public class PassivePluginMatcher {

    @Autowired
    UserPermissionManager permissionManager;

    @Autowired
    PluginEnableProcessor pluginEnableProcessor;

    @Autowired
    SavePluginCallEvent savePluginCallEvent;

    /**
     * 处理消息事件
     * @param event  消息事件
     */
    public void matchMessageEvent(YuniMessageEvent event, PluginContainer pluginContainer) {
        List<PassivePluginInstance> matchedPluginInstances = new ArrayList<>();

        // 先匹配指令，如果匹配到了指令，那么不再继续匹配模式
        for (PassivePluginInstance instance : pluginContainer.getCommandPlugins().values()) {
            // 权限检查与使能情况检查
            if (isPluginEnabled(event, instance) && checkPermission(instance, event)) {
                CommandDetector detector = (CommandDetector) instance.getDetector();
                if (detector.match(event)) {
                    log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                    matchedPluginInstances.add(instance);
                }
            }

        }
        if (!matchedPluginInstances.isEmpty()) {
            executeCommandPlugin(matchedPluginInstances, event);
            // 匹配到指令后不再继续匹配其他插件
            return;
        }
        for (PassivePluginInstance instance : pluginContainer.getPatternPlugins().values()) {
            // 权限检查
            if (isPluginEnabled(event, instance) && checkPermission(instance, event)) {
                PatternDetector detector = (PatternDetector) instance.getDetector();
                if (detector.match(event)) {
                    log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                    matchedPluginInstances.add(instance);
                }
            }
        }
        executePatternPlugin(matchedPluginInstances, event);
    }

    private void executeCommandPlugin(List<PassivePluginInstance> pluginInstances, YuniMessageEvent event) {
        for (PassivePluginInstance instance : pluginInstances) {
            CompletableFuture.runAsync(() -> {
                savePluginCallEvent.saveEvent(event, instance);
                try {
                    instance.getExecuteMethod().invoke(instance.getPassivePlugin(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void executePatternPlugin(List<PassivePluginInstance> pluginInstances, YuniMessageEvent event) {
        for (PassivePluginInstance instance : pluginInstances) {
            CompletableFuture.runAsync(() -> {
                // 被动插件的调用暂时不记录到数据库
                try {
                    instance.getExecuteMethod().invoke(instance.getPassivePlugin(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 权限检查
     * @param instance 被动插件实例
     * @param event  事件
     * @return 是否通过
     */
    private boolean checkPermission(PassivePluginInstance instance, YuniMessageEvent event) {
        // 获取用户权限
        UserPermission userPermission = permissionManager.getUserPermission(event, instance.getPluginMetadata().getId());
        UserPermission requiredPermission = instance.getPermission();

        return userPermission.getPriority() >= requiredPermission.getPriority();
    }


    // 检查插件是否使能
    public Boolean isPluginEnabled(YuniMessageEvent event, PluginInstance instance) {
        return pluginEnableProcessor.isPluginEnabled(event, instance);
    }

    public void matchNoticeEvent(YuniNoticeEvent event, PluginContainer pluginContainer) {

    }

    public void matchRequestEvent(YuniRequestEvent event, PluginContainer pluginContainer) {

    }

    public void matchMetaEvent(YuniMetaEvent event, PluginContainer pluginContainer) {

    }
}
