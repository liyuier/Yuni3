package com.yuier.yuni.plugin.manage.enable;

import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.notice.YuniNoticeEvent;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import com.yuier.yuni.plugin.service.GroupPluginAbilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: PluginEnableProcessor
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2025/12/24 21:06
 * @description: 管理插件在某定位上的启停
 */

@Component
public class PluginEnableProcessor {

    @Autowired
    GroupPluginAbilityService groupPluginAbilityService;
    @Autowired
    PluginContainer pluginContainer;

    Map<String, Boolean> pluginEnableMap = new HashMap<>(); // TODO initialize

    // 判断插件是否使能
    public Boolean isPluginEnabled(YuniMessageEvent event, PluginInstance instance) {
        // ① 特定群例外设置（最高优先级）
        Boolean pluginEnabledException = getPluginEnabledException(event.getGroupId(), instance.getPluginFullId());
        if (pluginEnabledException != null) {
            return pluginEnabledException;
        }
        // ② 全局例外设置（次优先级，groupId = null）
        Boolean globalException = getPluginEnabledException(null, instance.getPluginFullId());
        if (globalException != null) {
            return globalException;
        }
        // ③ 最终兜底：插件元数据中的默认启停状态
        return getPluginDefaultEnabled(instance);
    }

    public boolean isPluginEnabled(YuniNoticeEvent event, PassivePluginInstance instance) {
        // ① 特定群例外设置（最高优先级）
        Boolean pluginEnabledException = getPluginEnabledException(event.getGroupId(), instance.getPluginFullId());
        if (pluginEnabledException != null) {
            return pluginEnabledException;
        }
        // ② 全局例外设置（次优先级，groupId = null）
        Boolean globalException = getPluginEnabledException(null, instance.getPluginFullId());
        if (globalException != null) {
            return globalException;
        }
        // ③ 最终兜底：插件元数据中的默认启停状态
        return getPluginDefaultEnabled(instance);
    }

    // 获取插件默认使能情况
    private Boolean getPluginDefaultEnabled(PluginInstance instance) {
        return instance.getPluginMetadata().getDefaultEnable();
    }

    // 查找插件使能的特殊情况
    public Boolean getPluginEnabledException(Long groupId, String pluginId) {
        // 先在缓存里查找，如果缓存里没有，就返回 null
        String pluginEnableKey = assemblePluginEnableKey(groupId, pluginId);
        if (pluginEnableMap.containsKey(pluginEnableKey)) {
            return pluginEnableMap.get(pluginEnableKey);
        }
        // 缓存里没有，再去数据库里查
        Boolean pluginEnabled = groupPluginAbilityService.getPluginAbility(groupId, pluginId);
        if (pluginEnabled != null) {
            pluginEnableMap.put(pluginEnableKey, pluginEnabled);
            return pluginEnabled;
        }
        // 数据库里也没有，返回 null
        return null;
    }

    // 根据群组ID、插件ID组装键
    public String assemblePluginEnableKey(Long groupId, String pluginId) {
        return groupId + "@" + pluginId;
    }

    public void enablePlugin(YuniMessageEvent eventContext, String pluginId) {
        enablePlugin(eventContext.getGroupId(), pluginId);
    }

    public void enablePlugin(PluginEnableEvent eventContext, Class<? extends YuniPlugin> pluginClazz) {
        String pluginId = pluginContainer.getPluginFullIdByPluginClass(pluginClazz);
        enablePlugin(eventContext.getGroupId(), pluginId);
    }

    public void enablePlugin(Long groupId, String pluginId) {
        groupPluginAbilityService.enablePlugin(groupId, pluginId);
        // 刷新缓存
        pluginEnableMap.put(assemblePluginEnableKey(groupId, pluginId), true);
    }

    public void disablePlugin(YuniMessageEvent eventContext, String pluginId) {
        disablePlugin(eventContext.getGroupId(), pluginId);
    }

    public void disablePlugin(PluginDisableEvent eventContext, Class<? extends YuniPlugin> pluginClazz) {
        String pluginId = pluginContainer.getPluginFullIdByPluginClass(pluginClazz);
        disablePlugin(eventContext.getGroupId(), pluginId);
    }

    private void disablePlugin(Long groupId, String pluginId) {
        groupPluginAbilityService.disablePlugin(groupId, pluginId);
        // 刷新缓存
        pluginEnableMap.put(assemblePluginEnableKey(groupId, pluginId), false);
    }

    public Boolean isPluginEnabled(Long groupId, Class<? extends YuniPlugin> pluginClazz) {
        String pluginId = pluginContainer.getPluginFullIdByPluginClass(pluginClazz);
        PluginInstance pluginInstance = pluginContainer.getPluginInstanceByFullId(pluginId);
        // ① 特定群例外设置（最高优先级）
        Boolean pluginEnabledException = getPluginEnabledException(groupId, pluginInstance.getPluginFullId());
        if (pluginEnabledException != null) {
            return pluginEnabledException;
        }
        // ② 全局例外设置（次优先级，groupId = null）
        Boolean globalException = getPluginEnabledException(null, pluginInstance.getPluginFullId());
        if (globalException != null) {
            return globalException;
        }
        // ③ 最终兜底：插件元数据中的默认启停状态
        return getPluginDefaultEnabled(pluginInstance);
    }
}
