package com.yuier.yuni.plugin.manage.load;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.event.context.SpringYuniEvent;
import com.yuier.yuni.event.detector.YuniEventDetector;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePluginInstance;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePluginInterface;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInterface;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import com.yuier.yuni.plugin.util.PluginBusinessMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @Title: PluginInstanceFactory
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage.load
 * @Date 2026/1/6 16:38
 * @description: 插件实例工厂
 */

@Component
@Slf4j
public class PluginInstanceFactory {

    private final Map<YuniPluginType, BiFunction<PluginMetadata, YuniPlugin, PluginInstance>> pluginInstanceCreatorMap;

    public PluginInstanceFactory() {
        this.pluginInstanceCreatorMap = new ConcurrentHashMap<>();
        this.pluginInstanceCreatorMap.put(YuniPluginType.SCHEDULED, this::createScheduledPluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.IMMEDIATE, this::createImmediatePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.COMMAND, this::createPassivePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.PATTERN, this::createPassivePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.NOTICE, this::createPassivePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.REQUEST, this::createPassivePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.META, this::createPassivePluginInstance);
        this.pluginInstanceCreatorMap.put(YuniPluginType.MESSAGE_SENT, this::createPassivePluginInstance);
    }

    /**
     * 创建插件实例
     * @param metadata 插件元数据
     * @param pluginClass 插件类
     * @return 插件实例
     */
    public PluginInstance createPluginInstance(PluginMetadata metadata, Class<?> pluginClass) throws Exception {
        YuniPlugin plugin = (YuniPlugin) pluginClass.getDeclaredConstructor().newInstance();
        YuniPluginType pluginType = PluginBusinessMapUtil.getPluginType(plugin);
        if (pluginType == YuniPluginType.UNKNOWN) {
            log.error("插件类型未知，请检查插件 {} 的实现类是否继承了 YuniPlugin 接口", pluginClass.getName());
            return null;
        }
        return pluginInstanceCreatorMap.get(pluginType).apply(metadata, plugin);
    }

    /**
     * 创建定时任务插件实例
     * @param metadata 插件元数据
     * @param yuniPlugin 插件
     * @return 定时任务插件实例
     */
    public ScheduledPluginInstance createScheduledPluginInstance(PluginMetadata metadata, YuniPlugin yuniPlugin) {
        ScheduledPluginInterface scheduledPlugin = (ScheduledPluginInterface) yuniPlugin;
        ScheduledPluginInstance scheduledPluginInstance = new ScheduledPluginInstance();
        scheduledPluginInstance.setPluginMetadata(metadata);
        scheduledPluginInstance.setPlugin(scheduledPlugin);
        scheduledPluginInstance.setAction(scheduledPlugin.getAction());
        scheduledPluginInstance.setPlugin(scheduledPlugin);

        scheduledPluginInstance.setCronExpression(scheduledPlugin.cronExpression());
        return scheduledPluginInstance;
    }

    /**
     * 创建立即执行插件实例
     * @param metadata 插件元数据
     * @param yuniPlugin 插件
     * @return 立即执行插件实例
     */
    public ImmediatePluginInstance createImmediatePluginInstance(PluginMetadata metadata, YuniPlugin yuniPlugin) {
        ImmediatePluginInterface immediatePlugin = (ImmediatePluginInterface) yuniPlugin;
        ImmediatePluginInstance immediatePluginInstance = new ImmediatePluginInstance();
        immediatePluginInstance.setPluginMetadata(metadata);
        immediatePluginInstance.setPlugin(immediatePlugin);
        immediatePluginInstance.setAction(immediatePlugin.getAction());
        immediatePluginInstance.setPlugin(immediatePlugin);

        return immediatePluginInstance;
    }

    /**
     * 创建被动插件实例
     * @param metadata 插件元数据
     * @param yuniPlugin 插件
     * @return 被动插件实例
     */
    public PassivePluginInstance createPassivePluginInstance(PluginMetadata metadata, YuniPlugin yuniPlugin) {
        PassivePlugin<?, ?> passivePlugin = (PassivePlugin<?, ?>) yuniPlugin;
        PassivePluginInstance passivePluginInstance = new PassivePluginInstance();
        passivePluginInstance.setPluginMetadata(metadata);
        passivePluginInstance.setPlugin(passivePlugin);

        // 提取探测器
        YuniEventDetector<?> detector = passivePlugin.getDetector();
        passivePluginInstance.setDetector(detector);

        // 提取权限
        passivePluginInstance.setPermission(passivePlugin.pluginPermission());

        // 提取执行方法
        try {
            Method executeMethod = passivePlugin.getClass().getMethod("execute", SpringYuniEvent.class);
            passivePluginInstance.setExecuteMethod(executeMethod);
        } catch (NoSuchMethodException e) {
            log.error("插件 {} 没有执行方法！", passivePlugin.getClass().getName());
            return null;
        }

        return passivePluginInstance;
    }
}
