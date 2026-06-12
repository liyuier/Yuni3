package com.yuier.yuni.webapi.service.plugin;

import com.yuier.yuni.core.enums.YuniPluginType;
import com.yuier.yuni.core.task.DynamicTaskManager;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.manage.enable.PluginEnableProcessor;
import com.yuier.yuni.plugin.manage.enable.event.PluginDisableEvent;
import com.yuier.yuni.plugin.manage.enable.event.PluginEnableEvent;
import com.yuier.yuni.plugin.manage.load.PluginLoadProcessor;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import com.yuier.yuni.plugin.model.active.immediate.ImmediatePluginInstance;
import com.yuier.yuni.plugin.model.active.scheduled.ScheduledPluginInstance;
import com.yuier.yuni.webapi.dto.plugin.PluginInfo;
import com.yuier.yuni.webapi.dto.plugin.PluginModuleInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 插件管理服务。
 * 负责插件列表查询、启用/禁用、重载（单个/全部）等核心业务逻辑。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginService {

    private final PluginContainer pluginContainer;
    private final PluginEnableProcessor pluginEnableProcessor;
    private final PluginManager pluginManager;
    private final PluginLoadProcessor pluginLoadProcessor;
    private final DynamicTaskManager dynamicTaskManager;

    @Value("${bot.app.plugin.directory:plugins}")
    private String pluginDirectoryPath;

    // ==================== 查询 ====================

    /**
     * 获取插件列表，以模块为单位分组。
     * 遍历容器中所有已加载的插件实例，按 moduleId 分组后组装为 PluginModuleInfo 列表。
     * @return 插件模块列表，保持模块注册顺序
     */
    public List<PluginModuleInfo> listPlugins() {
        // 保持插入顺序
        Map<String, List<PluginInfo>> moduleMap = new LinkedHashMap<>();
        for (String fullId : pluginContainer.getPluginFullIds()) {
            PluginInstance instance = pluginContainer.getPluginInstanceByFullId(fullId);
            if (instance == null) {
                continue;
            }
            PluginMetadata meta = instance.getPluginMetadata();
            PluginInfo info = new PluginInfo(
                    fullId, meta.getName(), meta.getDescription(),
                    meta.getTips(),
                    meta.getVersion(), meta.getAuthor(), meta.getModuleId(),
                    instance.isBuiltIn(), instance.getPluginType().name());
            moduleMap.computeIfAbsent(meta.getModuleId(), k -> new ArrayList<>()).add(info);
        }

        List<PluginModuleInfo> result = new ArrayList<>();
        for (Map.Entry<String, List<PluginInfo>> entry : moduleMap.entrySet()) {
            // 模块名从模块下第一个插件的元数据中取
            PluginInstance first = pluginContainer.getPluginInstanceByFullId(
                    entry.getValue().get(0).getFullId());
            String moduleName = first != null
                    ? first.getPluginMetadata().getModuleName()
                    : entry.getKey();
            result.add(new PluginModuleInfo(entry.getKey(), moduleName, entry.getValue()));
        }
        return result;
    }

    // ==================== 启用 / 禁用 ====================

    /**
     * 启用插件。
     * 调用插件自身的 enable 生命周期回调，并将启用状态持久化到数据库与缓存。
     * @param pluginId 目标插件的全限定ID
     * @param groupId  目标群组ID，为 null 时全局启用
     * @throws IllegalArgumentException 插件不存在
     */
    public void enablePlugin(String pluginId, Long groupId) {
        PluginInstance instance = requirePlugin(pluginId);

        // 调用插件自身的 enable 生命周期方法
        instance.getPlugin().enable(new PluginEnableEvent(groupId, 0L));

        // 持久化启用状态（DB + 缓存）
        pluginEnableProcessor.enablePlugin(groupId, pluginId);

        log.info("已启用插件: {} (groupId={})", pluginId, groupId);
    }

    /**
     * 禁用插件。
     * 调用插件自身的 disable 生命周期回调，并将禁用状态持久化到数据库与缓存。
     * @param pluginId 目标插件的全限定ID
     * @param groupId  目标群组ID，为 null 时全局禁用
     * @throws IllegalArgumentException 插件不存在或目标为插件管理自身
     */
    public void disablePlugin(String pluginId, Long groupId) {
        PluginInstance instance = requirePlugin(pluginId);

        // 插件管理插件自身不可被禁用
        String pluginManageFullId = findPluginManageFullId();
        if (pluginId.equals(pluginManageFullId)) {
            throw new IllegalArgumentException("插件管理插件无法被禁用");
        }

        // 调用插件自身的 disable 生命周期方法
        instance.getPlugin().disable(new PluginDisableEvent(groupId, 0L));

        // 持久化禁用状态（DB + 缓存）
        pluginEnableProcessor.disablePlugin(
                new PluginDisableEvent(groupId, 0L),
                instance.getPluginClass());

        log.info("已禁用插件: {} (groupId={})", pluginId, groupId);
    }

    // ==================== 重载 ====================

    /**
     * 重载插件。
     * pluginId 为空时重载全部插件；非空时重载指定插件模块（对应一个 JAR 包）。
     * @param pluginId 目标插件的全限定ID，为空时重载全部
     * @throws IllegalArgumentException 插件不存在或 JAR 文件未找到
     * @throws RuntimeException        重载过程中发生异常
     */
    public void reloadPlugin(String pluginId) {
        if (pluginId == null || pluginId.isBlank()) {
            reloadAllPlugins();
            log.info("已重载全部插件");
            return;
        }

        // 校验插件存在
        PluginInstance instance = requirePlugin(pluginId);

        // 查找该插件对应的 JAR 文件
        File jarFile = findJarFileForPlugin(pluginId);
        if (jarFile == null) {
            throw new IllegalArgumentException(
                    "未找到插件对应的 JAR 文件，请检查插件目录: " + pluginId);
        }

        try {
            reloadSinglePlugin(jarFile);
            log.info("已重载插件: {} (jar: {})", pluginId, jarFile.getName());
        } catch (Exception e) {
            log.error("重载插件失败: {}", pluginId, e);
            throw new RuntimeException("重载插件失败: " + e.getMessage(), e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据插件全限定ID获取插件实例，不存在时抛异常。
     */
    private PluginInstance requirePlugin(String pluginId) {
        PluginInstance instance = pluginContainer.getPluginInstanceByFullId(pluginId);
        if (instance == null) {
            throw new IllegalArgumentException("未找到插件: " + pluginId);
        }
        return instance;
    }

    /**
     * 查找插件管理插件（PluginManage）的全限定ID。
     * 遍历容器中所有已注册插件，通过类名 PluginManage 匹配，避免编译期直接依赖插件 JAR。
     * @return PluginManage 的全限定ID，未找到时返回 null
     */
    private String findPluginManageFullId() {
        for (String fullId : pluginContainer.getPluginFullIds()) {
            PluginInstance instance = pluginContainer.getPluginInstanceByFullId(fullId);
            if (instance != null && "PluginManage".equals(instance.getPluginClass().getSimpleName())) {
                return fullId;
            }
        }
        return null;
    }

    /**
     * 重载全部插件。
     * 依次执行：销毁所有插件实例 → 取消全部定时任务 → 清空容器 → 从插件目录重新扫描并加载所有 JAR。
     */
    private void reloadAllPlugins() {
        for (PluginInstance instance : pluginContainer.getPluginFullIdToInstanceMap().values()) {
            instance.destroy();
        }
        dynamicTaskManager.cancelAllTasks();
        pluginContainer.clear();
        pluginManager.initializePlugins();
    }

    /**
     * 重载单个插件模块（对应一个 JAR 包）。
     * 完整执行流程：① 从 JAR 重新加载新模块与插件实例 → ② 销毁旧实例并从容器各数据结构中移除 →
     * ③ 取消旧定时任务 → ④ 刷新模块元数据 → ⑤ 在原位置插入新实例并重新注册主动插件 →
     * ⑥ 修正后续模块下所有插件的索引偏移。
     * 若该模块在容器中不存在（首次加载），则走 PluginManager 的常规注册流程。
     * @param jarFile 插件 JAR 文件
     */
    private void reloadSinglePlugin(File jarFile) throws Exception {
        // 1. 加载新的插件模块与插件实例
        PluginModuleInstance newModuleInstance = pluginLoadProcessor.assemblePluginModule(jarFile);
        List<Class<?>> pluginClasses = pluginLoadProcessor.loadPluginClassesFromJarFile(jarFile);
        List<PluginInstance> newPluginInstances = pluginLoadProcessor.assemblePluginInstances(newModuleInstance, pluginClasses);

        // 2. 获取模块下旧的插件实例
        String moduleId = newModuleInstance.getModuleId();
        List<PluginInstance> oldPluginInstances = pluginContainer.getPluginInstanceListByModuleId(moduleId);
        if (oldPluginInstances.isEmpty()) {
            pluginManager.loadAndRegisterPluginsFromSingleJarFile(jarFile);
            return;
        }

        int oldStartIndex = oldPluginInstances.get(0).getIndex();
        int insertPos = pluginContainer.getPluginFullIds().indexOf(oldPluginInstances.get(0).getPluginFullId());

        // 3. 销毁并移除旧的插件实例
        for (PluginInstance oldInstance : oldPluginInstances) {
            oldInstance.destroy();
            pluginContainer.getPluginTypeToFullIdListMap()
                    .get(oldInstance.getPluginType())
                    .remove(oldInstance.getPluginFullId());
            pluginContainer.getPluginFullIds().remove(oldInstance.getPluginFullId());
            pluginContainer.getPluginFullIdToInstanceMap().remove(oldInstance.getPluginFullId());
            pluginContainer.getPluginClassToPluginFullIdMap().remove(oldInstance.getPluginClass());

            if (oldInstance.getPluginType() == YuniPluginType.SCHEDULED) {
                dynamicTaskManager.cancelTask(oldInstance.getPluginFullId());
            }
        }

        // 4. 刷新模块元数据
        int moduleIndex = pluginContainer.getPluginModuleIds().indexOf(moduleId);
        PluginModuleInstance oldModuleInstance = pluginContainer.getPluginModuleInstanceMap().get(moduleId);
        oldModuleInstance.setPluginModuleMetadata(newModuleInstance.getPluginModuleMetadata());

        // 5. 将新插件实例插入到原来的位置
        for (int i = 0; i < newPluginInstances.size(); i++) {
            PluginInstance newInstance = newPluginInstances.get(i);
            pluginContainer.getPluginFullIds().add(insertPos + i, newInstance.getPluginFullId());
            pluginContainer.getPluginFullIdToInstanceMap().put(newInstance.getPluginFullId(), newInstance);
            pluginContainer.getPluginClassToPluginFullIdMap().put(newInstance.getPluginClass(), newInstance.getPluginFullId());
            newInstance.setIndex(oldStartIndex + i);
            pluginContainer.getPluginTypeToFullIdListMap()
                    .get(newInstance.getPluginType())
                    .add(newInstance.getPluginFullId());

            if (newInstance instanceof ActivePluginInstance active) {
                registerActivePlugin(active);
            }
        }

        // 6. 更新后续模块下所有插件的索引
        int sizeDelta = newPluginInstances.size() - oldPluginInstances.size();
        for (int i = moduleIndex + 1; i < pluginContainer.getPluginModuleIds().size(); i++) {
            String mid = pluginContainer.getPluginModuleIds().get(i);
            List<PluginInstance> instances = pluginContainer.getPluginInstanceListByModuleId(mid);
            for (PluginInstance pi : instances) {
                pi.setIndex(pi.getIndex() + sizeDelta);
            }
        }
    }

    /**
     * 注册主动插件，根据子类型分派到定时注册或即时注册。
     * 若插件元数据中 defaultEnable 为 false，则跳过注册流程。
     * @param instance 主动插件实例
     */
    private void registerActivePlugin(ActivePluginInstance instance) {
        if (!instance.getPluginMetadata().getDefaultEnable()) {
            log.info("主动插件 {} 默认不生效，已跳过执行流程", instance.getPluginName());
            return;
        }
        if (instance instanceof ScheduledPluginInstance scheduled) {
            registerScheduledPlugin(scheduled);
        } else if (instance instanceof ImmediatePluginInstance immediate) {
            registerImmediatePlugin(immediate);
        }
    }

    /**
     * 注册定时插件到动态任务管理器。
     * 根据插件实例中配置的 cron 表达式创建定时任务，任务执行体为插件的 Action.execute()。
     * @param instance 定时插件实例，携带 cronExpression 与 Action
     */
    private void registerScheduledPlugin(ScheduledPluginInstance instance) {
        String pluginId = instance.getPluginFullId();
        Runnable task = () -> {
            try {
                instance.getAction().execute();
            } catch (Exception e) {
                log.error("执行主动插件失败: {}", pluginId, e);
            }
        };
        dynamicTaskManager.addCronTask(pluginId, instance.getCronExpression(), task);
    }

    /**
     * 注册即时插件，通过 CompletableFuture 异步执行一次后即结束。
     * @param instance 即时插件实例，携带 Action
     */
    private void registerImmediatePlugin(ImmediatePluginInstance instance) {
        CompletableFuture.runAsync(() -> {
            instance.getAction().execute();
            log.info("即时插件 {} 执行完毕", instance.getPluginName());
        });
    }

    /**
     * 根据插件全限定ID查找对应的 JAR 文件。
     * 通过插件容器获取所属模块的 moduleId 与 jarFileName，然后遍历插件目录递归匹配。
     * 匹配策略：文件名完全等于 jarFileName，或以 moduleId 为前缀的 .jar 文件。
     * 若匹配到多个候选 JAR 则返回 null（视为冲突），仅唯一匹配时返回。
     * @param pluginFullId 插件的全限定ID
     * @return 匹配到的 JAR 文件，未找到或多重匹配时返回 null
     */
    private File findJarFileForPlugin(String pluginFullId) {
        PluginModuleInstance moduleInstance = pluginContainer.getPluginModuleByPluginFullId(pluginFullId);
        if (moduleInstance == null) {
            return null;
        }
        String moduleId = moduleInstance.getModuleId();
        String jarFileName = moduleInstance.getJarFileName();

        File pluginDir = new File(pluginDirectoryPath);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", pluginDirectoryPath);
            return null;
        }

        Path rootPath = Paths.get(pluginDirectoryPath);
        try (Stream<Path> walkStream = Files.walk(rootPath)) {
            List<File> matches = walkStream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".jar"))
                    .map(Path::toFile)
                    .filter(file -> file.getName().equals(jarFileName)
                            || file.getName().startsWith(moduleId))
                    .toList();

            if (matches.isEmpty()) {
                log.warn("未找到插件 {} 对应的 JAR 文件 (moduleId={}, jarName={})",
                        pluginFullId, moduleId, jarFileName);
                return null;
            }
            if (matches.size() > 1) {
                log.warn("找到多个插件 {} 可能对应的 JAR 文件: {}",
                        pluginFullId, matches.stream().map(File::getName).toList());
                return null;
            }
            return matches.get(0);
        } catch (IOException e) {
            log.error("遍历插件目录时发生错误: {}", pluginDirectoryPath, e);
            return null;
        }
    }
}
