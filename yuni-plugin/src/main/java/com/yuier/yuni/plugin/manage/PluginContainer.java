package com.yuier.yuni.plugin.manage;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: PluginContainer
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage
 * @Date 2026/1/6 3:21
 * @description: 用于重构插件系统的新容器
 */

@Data
@Component
public class PluginContainer {

    // 保存插件或插件模块的 ID
    private final List<String> pluginModuleIds = new ArrayList<>();
    private final List<String> pluginFullIds = new ArrayList<>();

    // 为不同类型的插件分别保存 ID , 方便特定情况下的查找
    private final List<String> scheduledPluginFullIds = new ArrayList<>();
    private final List<String> immediatePluginFullIds = new ArrayList<>();
    private final List<String> commandPluginFullIds = new ArrayList<>();
    private final List<String> patternPluginFullIds = new ArrayList<>();
    private final List<String> noticePluginFullIds = new ArrayList<>();
    private final List<String> requestPluginFullIds = new ArrayList<>();
    private final List<String> metaPluginFullIds = new ArrayList<>();
    // 初始化插件类型到上面 Map 的映射
    private final Map<YuniPluginType, List<String>> pluginFullIdToModuleIdMap = new ConcurrentHashMap<>(Map.of(
            YuniPluginType.SCHEDULED, scheduledPluginFullIds,
            YuniPluginType.IMMEDIATE, immediatePluginFullIds,
            YuniPluginType.COMMAND, commandPluginFullIds,
            YuniPluginType.PATTERN, patternPluginFullIds,
            YuniPluginType.NOTICE, noticePluginFullIds,
            YuniPluginType.REQUEST, requestPluginFullIds,
            YuniPluginType.META, metaPluginFullIds
    ));

    // 保存插件实例，使用全限定 ID 作为键
    private final Map<String, PluginInstance> pluginFullIdToInstanceMap = new ConcurrentHashMap<>();
    // 插件模块实例，使用模块 ID 作为键
    private final Map<String, PluginModuleInstance> pluginModuleInstanceMap = new ConcurrentHashMap<>();

    private final Map<Class<? extends YuniPlugin>, String> pluginClassToPluginFullIdMap = new ConcurrentHashMap<>();
    
    /* setters */

    /**
     * 添加插件模块实例
     * @param pluginModuleInstance 插件模块实例
     */
    public void addPluginModule(PluginModuleInstance pluginModuleInstance) {
        pluginModuleIds.add(pluginModuleInstance.getModuleId());
        pluginModuleInstanceMap.put(pluginModuleInstance.getModuleId(), pluginModuleInstance);
    }

    /**
     * 添加插件实例
     * @param pluginInstances 插件实例
     */
    public void addPluginList(List<PluginInstance> pluginInstances) {
        for (PluginInstance pluginInstance : pluginInstances) {
            addPlugin(pluginInstance);
        }
    }

    /**
     * 添加插件实例
     * @param pluginInstance 插件实例
     */
    public void addPlugin(PluginInstance pluginInstance) {
        // 维护全插件名称列表
        pluginFullIds.add(pluginInstance.getPluginFullId());
        // 维护全插件实例 Map
        pluginFullIdToInstanceMap.put(pluginInstance.getPluginFullId(), pluginInstance);
        // 维护全插件 Class 到全插件 ID 的映射
        pluginClassToPluginFullIdMap.put(pluginInstance.getPluginClass(), pluginInstance.getPluginFullId());
        // 维护插件索引
        pluginInstance.setIndex(pluginFullIds.size());
        // 维护类型插件列表
        pluginFullIdToModuleIdMap.get(pluginInstance.getPluginType()).add(pluginInstance.getPluginFullId());
    }

    /**
     * 刷新插件实例
     * @param oldPluginInstance 旧插件实例
     * @param newPluginInstance 新插件实例
     */
    public void refreshPluginInstance(PluginInstance oldPluginInstance, PluginInstance newPluginInstance) {
        pluginFullIdToInstanceMap.put(oldPluginInstance.getPluginFullId(), newPluginInstance);
        pluginClassToPluginFullIdMap.remove(oldPluginInstance.getPluginClass());
        pluginClassToPluginFullIdMap.put(newPluginInstance.getPluginClass(), newPluginInstance.getPluginFullId());
    }

    /**
     * 刷新插件元数据
     * @param pluginFullId 插件全限定 ID
     * @param newPluginMetadata 新插件元数据
     */
    public void refreshPluginMetadata(String pluginFullId, PluginMetadata newPluginMetadata) {
        List<PluginMetadata> pluginMetadataList = getModuleByPluginFullId(pluginFullId).getPluginModuleMetadata().getPlugins();
        for (int i = 0; i < pluginMetadataList.size(); i++) {
            if (pluginMetadataList.get(i).getFullId().equals(pluginFullId)) {
                pluginMetadataList.set(i, newPluginMetadata);
                break;
            }
        }
    }
    
    /* getters */

    public int getPluginCount() {
        return pluginFullIds.size();
    }

    /**
     * 通过索引获取插件全限定 ID
     * @param index 索引
     * @return 插件全限定 ID
     */
    public String getPluginFullIdByIndex(int index) {
        return pluginFullIds.get(index);
    }

    /**
     * 通过索引获取插件实例
     * @param index 索引
     * @return 插件实例
     */
    public PluginInstance getPluginInstanceByIndex(int index) {
        return getPluginInstanceByFullId(getPluginFullIdByIndex(index));
    }

    /**
     * 通过插件全限定 ID 获取插件实例
     * @param pluginFullId 插件全限定 ID
     * @return 插件实例
     */
    public PluginInstance getPluginInstanceByFullId(String pluginFullId) {
        return pluginFullIdToInstanceMap.get(pluginFullId);
    }

    /**
     * 通过插件全限定 ID 获取模块 ID
     * @param pluginFullId 插件全限定 ID
     * @return 模块 ID
     */
    public String getModuleIdByPluginFullId(String pluginFullId) {
        PluginInstance pluginInstance = pluginFullIdToInstanceMap.get(pluginFullId);
        return pluginInstance.getPluginMetadata().getModuleId();
    }

    /**
     * 通过插件类获取插件全限定 ID
     * @param pluginClass 插件类
     * @return 插件全限定 ID
     */
    public String getPluginFullIdByPluginClass(Class<? extends YuniPlugin> pluginClass) {
        return pluginClassToPluginFullIdMap.get(pluginClass);
    }

    /**
     * 通过模块 ID 获取模块实例
     * @param moduleId 模块 ID
     * @return 模块实例
     */
    public PluginModuleInstance getPluginModuleInstanceByModuleId(String moduleId) {
        return pluginModuleInstanceMap.get(moduleId);
    }

    /**
     * 通过插件全限定 ID 获取模块实例
     * @param pluginFullId 插件全限定 ID
     * @return 模块实例
     */
    public PluginModuleInstance getModuleByPluginFullId(String pluginFullId) {
        return getPluginModuleInstanceByModuleId(getModuleIdByPluginFullId(pluginFullId));
    }

    /**
     * 通过模块 ID 获取插件列表
     * @param moduleId 模块 ID
     * @return 插件列表
     */
    public List<PluginInstance> getPluginInstanceListByModuleId(String moduleId) {
        List<PluginInstance> pluginInstances = new ArrayList<>();
        PluginModuleInstance pluginModuleInstance = getPluginModuleInstanceByModuleId(moduleId);
        for (String pluginFullId : pluginModuleInstance.getPluginFullIds()) {
            pluginInstances.add(getPluginInstanceByFullId(pluginFullId));
        }
        return pluginInstances;
    }

    /**
     * 清空容器
     */
    public void clear() {
        pluginModuleIds.clear();
        pluginFullIds.clear();

        scheduledPluginFullIds.clear();
        immediatePluginFullIds.clear();
        commandPluginFullIds.clear();
        patternPluginFullIds.clear();
        noticePluginFullIds.clear();
        requestPluginFullIds.clear();
        metaPluginFullIds.clear();

        pluginFullIdToInstanceMap.clear();
        pluginModuleInstanceMap.clear();
    }
}
