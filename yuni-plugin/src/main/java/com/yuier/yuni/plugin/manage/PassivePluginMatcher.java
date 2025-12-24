package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.command.CommandDetector;
import com.yuier.yuni.event.model.message.detector.pattern.PatternDetector;
import com.yuier.yuni.permission.manage.UserPermissionManager;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 处理消息事件
     * @param event  消息事件
     */
    public void matchMessageEvent(YuniMessageEvent event, PluginContainer pluginContainer) {
        // 先匹配指令，如果匹配到了指令，那么不再继续匹配模式
        boolean isCommand = false;
        for (PassivePluginInstance instance : pluginContainer.getCommandPlugins().values()) {
            // 权限检查与使能情况检查
            if (isPluginEnabled(event, instance) && checkPermission(instance, event)) {
                CommandDetector detector = (CommandDetector) instance.getDetector();
                if (detector.match(event)) {
                    isCommand = true;
                    log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                    try {
                        instance.getExecuteMethod().invoke(instance.getPassivePlugin(), event);
                    } catch (Exception e) {
                        log.error("执行被动插件失败: {}", instance.getPluginMetadata().getId(), e);
                    }
                }
            }
        }
        if (isCommand) {
            return;
        }
        for (PassivePluginInstance instance : pluginContainer.getPatternPlugins().values()) {
            // 权限检查
            if (isPluginEnabled(event, instance) && checkPermission(instance, event)) {
                PatternDetector detector = (PatternDetector) instance.getDetector();
                if (detector.match(event)) {
                    log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                    try {
                        instance.getExecuteMethod().invoke(instance.getPassivePlugin(), event);
                    } catch (Exception e) {
                        log.error("执行被动插件失败: {}", instance.getPluginMetadata().getId(), e);
                    }
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
    private boolean checkPermission(PassivePluginInstance instance, YuniMessageEvent event) {
        // 获取用户权限
        UserPermission userPermission = permissionManager.getUserPermissionException(event, instance.getPluginMetadata().getId());
        UserPermission requiredPermission = instance.getPermission();

        return userPermission.getPriority() >= requiredPermission.getPriority();
    }


    // 检查插件是否使能
    public Boolean isPluginEnabled(YuniMessageEvent event, PassivePluginInstance instance) {
        return pluginEnableProcessor.isPluginEnabled(event, instance);
    }
}
