package com.yuier.yuni.plugin.manage.match;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.context.YuniMessageSentEvent;
import com.yuier.yuni.event.context.meta.YuniMetaEvent;
import com.yuier.yuni.event.context.notice.YuniNoticeEvent;
import com.yuier.yuni.event.context.request.YuniRequestEvent;
import com.yuier.yuni.event.detector.message.command.CommandDetector;
import com.yuier.yuni.event.detector.message.pattern.PatternDetector;
import com.yuier.yuni.event.detector.meta.YuniMetaDetector;
import com.yuier.yuni.event.detector.notice.YuniNoticeDetector;
import com.yuier.yuni.event.detector.request.YuniRequestDetector;
import com.yuier.yuni.permission.manage.UserPermissionManager;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.enable.PluginEnableProcessor;
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
 * @Package com.yuier.yuni.plugin.manage.match
 * @Date 2026/1/6 21:35
 * @description: 事件匹配器
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
    @Autowired
    PluginContainer pluginContainer;

    /* 匹配消息事件 */

    /**
     * 匹配消息事件
     * @param event 消息事件
     */
    public void matchMessageEvent(YuniMessageEvent event) {
        List<PassivePluginInstance> matchedPluginInstances = new ArrayList<>();

        // 先匹配指令，如果匹配到了指令，那么不再继续匹配
        for (String commandPluginFullId : pluginContainer.getCommandPluginFullIds()) {
            PassivePluginInstance commandPluginInstance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(commandPluginFullId);
            // 权限检查与使能情况检查
            if (isPluginEnabled(event, commandPluginInstance) && checkPermission(commandPluginInstance, event)) {
                CommandDetector detector = (CommandDetector) commandPluginInstance.getDetector();
                if (detector.match(event)) {
                    log.info("匹配到插件: {}", commandPluginInstance.getPluginName());
                    matchedPluginInstances.add(commandPluginInstance);
                }
            }
        }
        if (!matchedPluginInstances.isEmpty()) {
            executePlugin(matchedPluginInstances, event, true);
            // 匹配到指令后不再继续匹配其他插件
            return;
        }
        // 再匹配模式类插件
        for (String patternPluginFullId : pluginContainer.getPatternPluginFullIds()) {
            PassivePluginInstance patternPluginInstance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(patternPluginFullId);
            // 权限检查
            if (isPluginEnabled(event, patternPluginInstance) && checkPermission(patternPluginInstance, event)) {
                PatternDetector detector = (PatternDetector) patternPluginInstance.getDetector();
                if (detector.match(event)) {
                    log.info("匹配到插件: {}", patternPluginInstance.getPluginMetadata().getName());
                    matchedPluginInstances.add(patternPluginInstance);
                }
            }
        }
        // 模式匹配的插件暂时不记录调用事件
        executePlugin(matchedPluginInstances, event, false);
    }

    /**
     * 执行插件
     * @param pluginInstances 插件实例列表
     * @param event 事件
     * @param saveCallEvent 是否保存调用事件
     */
    private void executePlugin(List<PassivePluginInstance> pluginInstances, YuniMessageEvent event, boolean saveCallEvent) {
        for (PassivePluginInstance instance : pluginInstances) {
            CompletableFuture.runAsync(() -> {
                if (saveCallEvent) {
                    savePluginCallEvent.saveEvent(event, instance);
                }
                try {
                    instance.getExecuteMethod().invoke(instance.getPlugin(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 检查插件是否使能
    public Boolean isPluginEnabled(YuniMessageEvent event, PluginInstance instance) {
        return pluginEnableProcessor.isPluginEnabled(event, instance);
    }

    private boolean isPluginEnabled(YuniNoticeEvent event, PassivePluginInstance instance) {
        return pluginEnableProcessor. isPluginEnabled(event, instance);
    }

    /**
     * 权限检查
     * @param instance 被动插件实例
     * @param event  事件
     * @return 是否通过
     */
    private boolean checkPermission(PassivePluginInstance instance, YuniMessageEvent event) {
        // 获取用户权限
        UserPermission userPermission = permissionManager.getUserPermission(event, instance.getPluginFullId());
        UserPermission requiredPermission = instance.getPermission();

        return userPermission.getPriority() >= requiredPermission.getPriority();
    }

    /* 匹配通知事件 */

    /**
     * 匹配通知事件
     * @param event 通知事件
     */
    public void matchNoticeEvent(YuniNoticeEvent event) {
        boolean matched = false;
        for (String noticePluginFullId : pluginContainer.getNoticePluginFullIds()) {
            PassivePluginInstance instance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(noticePluginFullId);
            if (isPluginEnabled(event, instance) && checkPermission(instance, event)) {
                YuniNoticeDetector detector = (YuniNoticeDetector) instance.getDetector();
                if (detector.match(event)) {
                    matched = true;
                    // 感觉有点屎山，但是没办法了，先这样吧
                    // 匹配后的 event 中保存了实际的通知事件类型
                    YuniNoticeEvent yuniNoticeEvent = event.getYuniNoticeEvent();
                    // 打个日志先
                    log.info(yuniNoticeEvent.toLogString());
                    log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                    // 跑一下，这里暂时先不像消息那样写，反正也不会有多少插件会处理通知事件的
                    CompletableFuture.runAsync(() -> {
                        try {
                            instance.getExecuteMethod().invoke(instance.getPlugin(), yuniNoticeEvent);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
        if (!matched) {
            log.info(event.toLogString());
        }
    }

    /**
     * 通知事件只检查用户是否被 BAN
     * @param instance 被动插件实例
     * @param event 事件
     * @return 是否通过
     */
    private boolean checkPermission(PassivePluginInstance instance, YuniNoticeEvent event) {
        // 获取用户权限
        UserPermission userPermission = permissionManager.getUserPermission(event, instance.getPluginFullId());
        return userPermission.getPriority() > UserPermission.BLOCKED.getPriority();
    }

    /**
     * 匹配请求事件
     * @param event 请求事件
     */
    public void matchRequestEvent(YuniRequestEvent event) {
        boolean matched = false;
        for (String requestPluginFullId : pluginContainer.getRequestPluginFullIds()) {
            PassivePluginInstance instance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(requestPluginFullId);
            YuniRequestDetector detector = (YuniRequestDetector) instance.getDetector();
            if (detector.match(event)) {
                matched = true;
                YuniRequestEvent yuniRequestEvent = event.getYuniRequestEvent();
                // 打个日志先
                log.info(yuniRequestEvent.toLogString());
                log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
                // 跑一下
                CompletableFuture.runAsync(() -> {
                    try {
                        instance.getExecuteMethod().invoke(instance.getPlugin(), yuniRequestEvent);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        // 没匹配到处理插件，打印默认日志
        if (!matched) {
            log.info(event.toLogString());
        }
    }

    /**
     * 匹配元事件
     * @param event 元事件
     */
    public void matchMetaEvent(YuniMetaEvent event) {
        boolean matched = false;
        for (String metaPluginFullId : pluginContainer.getMetaPluginFullIds()) {
            PassivePluginInstance instance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(metaPluginFullId);
            YuniMetaDetector detector = (YuniMetaDetector) instance.getDetector();
            if (detector.match(event)) {
                matched = true;
                YuniMetaEvent yuniMetaEvent = event.getYuniMetaEvent();
                // 打个日志先
                log.debug(yuniMetaEvent.toLogString());
                log.debug("匹配到插件: {}", instance.getPluginMetadata().getName());
                // 跑一下
                CompletableFuture.runAsync(() -> {
                    try {
                        instance.getExecuteMethod().invoke(instance.getPlugin(), yuniMetaEvent);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        // 没匹配到处理插件，打印默认日志
        if (!matched) {
            log.info(event.toLogString());
        }
    }

    /**
     * 匹配消息发送事件
     * @param event 消息发送事件
     */
    public void matchMessageSentEvent(YuniMessageSentEvent event) {
        for (String messageSentPluginFullId : pluginContainer.getMessageSentPluginFullIds()) {
            PassivePluginInstance instance = (PassivePluginInstance) pluginContainer.getPluginInstanceByFullId(messageSentPluginFullId);
            // 消息发送事件没什么好匹配的，直接执行就行 TODO 上面的执行逻辑也要优化一下
            log.info("匹配到插件: {}", instance.getPluginMetadata().getName());
            CompletableFuture.runAsync(() -> {
                try {
                    instance.getExecuteMethod().invoke(instance.getPlugin(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
