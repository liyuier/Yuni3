package com.yuier.yuni.webapi.controller.plugin;

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
import com.yuier.yuni.webapi.dto.plugin.PluginActionReq;
import com.yuier.yuni.webapi.dto.plugin.PluginInfo;
import com.yuier.yuni.webapi.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/admin/plugin")
public class AdminPluginController {

    @Autowired
    private PluginContainer pluginContainer;

    @Autowired
    private PluginEnableProcessor pluginEnableProcessor;

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private PluginLoadProcessor pluginLoadProcessor;

    @Autowired
    private DynamicTaskManager dynamicTaskManager;

    @Value("${bot.app.plugin.directory:plugins}")
    private String pluginDirectoryPath;

    /**
     * 获取插件列表
     * @return 插件列表
     */
    @PostMapping("/list")
    public Result<List<PluginInfo>> pluginList() {
        List<PluginInfo> list = new ArrayList<>();
        for (String fullId : pluginContainer.getPluginFullIds()) {
            PluginInstance instance = pluginContainer.getPluginInstanceByFullId(fullId);
            if (instance == null) continue;
            PluginMetadata meta = instance.getPluginMetadata();
            list.add(new PluginInfo(
                    fullId, meta.getName(), meta.getDescription(),
                    meta.getVersion(), meta.getAuthor(), meta.getModuleId(),
                    instance.isBuiltIn(), instance.getPluginType().name()));
        }
        return Result.ok(list);
    }

    /**
     * 启用插件
     * @param req 请求体，其中包括插件 ID 与 群组 ID
     *            群组 ID 为空时全局操作，非空时针对指定群组
     * @return 操作成功
     */
    @PostMapping("/enable")
    public Result<Void> pluginEnable(@RequestBody PluginActionReq req) {
        // 校验插件 ID
        if (req.getPluginId() == null || req.getPluginId().isBlank()) {
            return Result.fail(400, "插件ID不能为空");
        }

        // 查找插件实例
        PluginInstance instance = pluginContainer.getPluginInstanceByFullId(req.getPluginId());
        if (instance == null) {
            return Result.fail(404, "未找到插件: " + req.getPluginId());
        }

        // 调用插件自身的 enable 生命周期方法
        instance.getPlugin().enable(new PluginEnableEvent(req.getGroupId(), 0L));

        // 持久化启用状态（DB + 缓存）
        pluginEnableProcessor.enablePlugin(req.getGroupId(), req.getPluginId());

        log.info("已启用插件: {} (groupId={})", req.getPluginId(), req.getGroupId());
        return Result.ok(null, "插件已启用");
    }

    /**
     * 禁用插件
     * @param req 插件禁用请求体，其中包括插件 ID 与 群组 ID
     *            群组 ID 为空时全局操作，非空时针对指定群组
     * @return 操作成功
     */
    @PostMapping("/disable")
    public Result<Void> pluginDisable(@RequestBody PluginActionReq req) {
        // 校验插件 ID
        if (req.getPluginId() == null || req.getPluginId().isBlank()) {
            return Result.fail(400, "插件ID不能为空");
        }

        // 查找插件实例
        PluginInstance instance = pluginContainer.getPluginInstanceByFullId(req.getPluginId());
        if (instance == null) {
            return Result.fail(404, "未找到插件: " + req.getPluginId());
        }

        // 插件管理插件自身不可被禁用
        String pluginManageFullId = findPluginManageFullId();
        if (req.getPluginId().equals(pluginManageFullId)) {
            return Result.fail(400, "插件管理插件无法被禁用");
        }

        // 调用插件自身的 disable 生命周期方法
        instance.getPlugin().disable(new PluginDisableEvent(req.getGroupId(), 0L));

        // 持久化禁用状态（DB + 缓存）
        pluginEnableProcessor.disablePlugin(
                new PluginDisableEvent(req.getGroupId(), 0L),
                instance.getPluginClass());

        log.info("已禁用插件: {} (groupId={})", req.getPluginId(), req.getGroupId());
        return Result.ok(null, "插件已禁用");
    }

    /**
     * 重载插件
     * @param req 插件重载请求体，其中包括插件 ID
     * @return 操作成功
     */
    @PostMapping("/reload")
    public Result<Void> pluginReload(@RequestBody PluginActionReq req) {
        // pluginId 为空时，重载全部插件
        if (req.getPluginId() == null || req.getPluginId().isBlank()) {
            reloadAllPlugins();
            log.info("已重载全部插件");
            return Result.ok(null, "已重载所有插件");
        }

        // 重载指定插件：先查找插件实例
        PluginInstance instance = pluginContainer.getPluginInstanceByFullId(req.getPluginId());
        if (instance == null) {
            return Result.fail(404, "未找到插件: " + req.getPluginId());
        }

        // 查找该插件对应的 JAR 文件
        File jarFile = findJarFileForPlugin(req.getPluginId());
        if (jarFile == null) {
            return Result.fail(404, "未找到插件对应的 JAR 文件，请检查插件目录: " + req.getPluginId());
        }

        try {
            reloadSinglePlugin(jarFile);
            log.info("已重载插件: {} (jar: {})", req.getPluginId(), jarFile.getName());
            return Result.ok(null, "已重载插件: " + instance.getPluginName());
        } catch (Exception e) {
            log.error("重载插件失败: {}", req.getPluginId(), e);
            return Result.fail(500, "重载插件失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 查找插件管理插件（PluginManage）的全限定ID。
     * 遍历容器中所有已注册插件，通过类名 PluginManage 匹配，避免编译期直接依赖插件 JAR。
     * 无参数。
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
     * 无参数。
     * 无返回值。
     */
    private void reloadAllPlugins() {
        // 销毁所有插件实例
        for (PluginInstance instance : pluginContainer.getPluginFullIdToInstanceMap().values()) {
            instance.destroy();
        }
        // 取消所有定时任务
        dynamicTaskManager.cancelAllTasks();
        // 清空容器
        pluginContainer.clear();
        // 重新加载全部插件
        pluginManager.initializePlugins();
    }

    /**
     * 重载单个插件模块（对应一个 JAR 包）。
     * 完整执行流程：① 从 JAR 重新加载新模块与插件实例 → ② 销毁旧实例并从容器各数据结构中移除 → ③ 取消旧定时任务 →
     * ④ 刷新模块元数据 → ⑤ 在原位置插入新实例并重新注册主动插件 → ⑥ 修正后续模块下所有插件的索引偏移。
     * 若该模块在容器中不存在（首次加载），则走 PluginManager 的常规注册流程。
     * @param jarFile 插件 JAR 文件，由 {@link #findJarFileForPlugin(String)} 定位得出
     * 无返回值，异常直接向上抛出由调用方处理。
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
            // 模块尚不存在于容器中，直接注册为新的
            pluginManager.loadAndRegisterPluginsFromSingleJarFile(jarFile);
            return;
        }

        int oldStartIndex = oldPluginInstances.get(0).getIndex();
        int insertPos = pluginContainer.getPluginFullIds().indexOf(oldPluginInstances.get(0).getPluginFullId());

        // 3. 销毁并移除旧的插件实例
        for (PluginInstance oldInstance : oldPluginInstances) {
            oldInstance.destroy();
            // 从分类型 ID 列表中移除
            pluginContainer.getPluginTypeToFullIdListMap()
                    .get(oldInstance.getPluginType())
                    .remove(oldInstance.getPluginFullId());
            // 从全量 ID 列表中移除
            pluginContainer.getPluginFullIds().remove(oldInstance.getPluginFullId());
            // 从实例 Map 中移除
            pluginContainer.getPluginFullIdToInstanceMap().remove(oldInstance.getPluginFullId());
            // 从 Class → ID 映射中移除
            pluginContainer.getPluginClassToPluginFullIdMap().remove(oldInstance.getPluginClass());

            // 如果是定时插件，取消定时任务
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
            // 全量 ID 列表（插入到原位置）
            pluginContainer.getPluginFullIds().add(insertPos + i, newInstance.getPluginFullId());
            // 实例 Map
            pluginContainer.getPluginFullIdToInstanceMap().put(newInstance.getPluginFullId(), newInstance);
            // Class → ID 映射
            pluginContainer.getPluginClassToPluginFullIdMap().put(newInstance.getPluginClass(), newInstance.getPluginFullId());
            // 索引
            newInstance.setIndex(oldStartIndex + i);
            // 分类型 ID 列表
            pluginContainer.getPluginTypeToFullIdListMap()
                    .get(newInstance.getPluginType())
                    .add(newInstance.getPluginFullId());

            // 主动插件需要特殊处理
            if (newInstance instanceof ActivePluginInstance) {
                registerActivePlugin((ActivePluginInstance) newInstance);
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
     * @param instance 主动插件实例，可能是 {@link ScheduledPluginInstance} 或 {@link ImmediatePluginInstance}
     * 无返回值。
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
     * 无返回值。
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
     * 无返回值，执行结果通过日志记录。
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
