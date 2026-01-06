package util;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.manage.load.PluginLoadProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.util.PluginUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * @Title: PluginReloadProcessor
 * @Author yuier
 * @Package util
 * @Date 2026/1/7 4:01
 * @description: 重载插件
 */

@Slf4j
@NoArgsConstructor
public class PluginReloadProcessor {

    /**
     * 重载指定插件
     * @param pluginFullId 插件全 ID
     * @param jarFile 插件 jar 包
     */
    public void reloadPlugin(String pluginFullId, File jarFile) {
        PluginLoadProcessor pluginLoadProcessor = PluginUtils.getBean(PluginLoadProcessor.class);
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        try {
            // 先加载出新的插件模块与插件列表
            PluginModuleInstance newModuleInstance = pluginLoadProcessor.assemblePluginModule(jarFile);
            List<Class<?>> pluginClasses = pluginLoadProcessor.loadPluginClassesFromJarFile(jarFile);
            List<PluginInstance> newPluginInstances = pluginLoadProcessor.assemblePluginInstances(newModuleInstance, pluginClasses);
            /* 再操作容器 */

            // 获取旧的插件实例列表
            String oldModuleId = newModuleInstance.getModuleId();
            List<PluginInstance> oldPluginInstances = container.getPluginInstanceListByModuleId(oldModuleId);
            int oldStartIndex = oldPluginInstances.get(0).getIndex();
            int oldStartIndexInFullIdList = container.getPluginFullIds().indexOf(oldPluginInstances.get(0).getPluginFullId());
            for (PluginInstance pluginInstance : oldPluginInstances) {
                // 调用 destroy 方法
                pluginInstance.destroy();
                // 删除插件分类型 ID
                container.getPluginTypeToFullIdListMap().get(pluginInstance.getPluginType())
                        .remove(pluginInstance.getPluginFullId());
                // 删除插件全量 ID 列表下的 ID
                container.getPluginFullIds().remove(pluginInstance.getPluginFullId());
                // 删除插件大 Map 中的实例
                container.getPluginFullIdToInstanceMap().remove(pluginInstance.getPluginFullId());
                // 删除插件 Class 到全插件 ID 的映射
                container.getPluginClassToPluginFullIdMap().remove(pluginInstance.getPluginClass());

                // 如果是定时插件，还需要删除定时任务
                if (pluginInstance.getPluginType() == YuniPluginType.SCHEDULED) {
                    DynamicTaskManager dynamicTaskManager = PluginUtils.getBean(DynamicTaskManager.class);
                    dynamicTaskManager.cancelTask(pluginInstance.getPluginFullId());
                }
            }

            // 刷新模块
            int moduleIndex = container.getPluginModuleIds().indexOf(oldModuleId);
            PluginModuleInstance oldModuleInstance = container.getPluginModuleInstanceMap().get(oldModuleId);
            // 刷新模块下存储的元数据
            oldModuleInstance.setPluginModuleMetadata(newModuleInstance.getPluginModuleMetadata());

            // 刷新模块下的插件
            for (PluginInstance newPluginInstance : newPluginInstances) {
                // 维护全插件名称列表
                container.getPluginFullIds().add(oldStartIndexInFullIdList, newPluginInstance.getPluginFullId());
                oldStartIndexInFullIdList ++;
                // 维护全插件实例 Map
                container.getPluginFullIdToInstanceMap().put(newPluginInstance.getPluginFullId(), newPluginInstance);
                // 维护全插件 Class 到全插件 ID 的映射
                container.getPluginClassToPluginFullIdMap().put(newPluginInstance.getPluginClass(), newPluginInstance.getPluginFullId());
                // 维护插件索引
                newPluginInstance.setIndex(oldStartIndex + newPluginInstances.indexOf(newPluginInstance));
                // 维护类型插件列表
                container.getPluginTypeToFullIdListMap().get(newPluginInstance.getPluginType()).add(newPluginInstance.getPluginFullId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载所有插件
     */
    public void reloadAllPlugins() {
        PluginContainer container = PluginUtils.getBean(PluginContainer.class);
        for (PluginInstance pluginInstance : container.getPluginFullIdToInstanceMap().values()) {
            // 调用 destroy 方法
            pluginInstance.destroy();
        }
        // 取消所有定时任务
        DynamicTaskManager dynamicTaskManager = PluginUtils.getBean(DynamicTaskManager.class);
        dynamicTaskManager.cancelAllTasks();
        container.clear();
        // 重新加载所有插件
        PluginManager pluginManager = PluginUtils.getBean(PluginManager.class);
        pluginManager.initializePlugins();
    }
}
