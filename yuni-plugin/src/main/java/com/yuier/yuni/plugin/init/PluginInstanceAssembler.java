package com.yuier.yuni.plugin.init;

/**
 * @Title: PluginInstanceAssembler
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 21:57
 * @description: 插件组装器
 */

import com.yuier.yuni.event.model.context.SpringYuniEvent;
import com.yuier.yuni.event.model.message.detector.YuniEventDetector;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.active.ScheduledPlugin;
import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * PluginInstance 组装器，负责从 JAR 包中扫描并组装 PluginInstance
 */

@Component
@Slf4j
public class PluginInstanceAssembler {

    private final PluginClassLoaderFactory classLoaderFactory;
    private final PluginMetadataParser metadataParser;

    public PluginInstanceAssembler(PluginClassLoaderFactory classLoaderFactory,
                                   PluginMetadataParser metadataParser) {
        this.classLoaderFactory = classLoaderFactory;
        this.metadataParser = metadataParser;
    }

    public File[] loadPluginJars(String pluginDirectoryPath) {
        File pluginDir = new File(pluginDirectoryPath);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", pluginDirectoryPath);
            return new File[] {};
        }

        // 使用 FilenameFilter 过滤 jar 包
        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            log.warn("插件目录为空: {}", pluginDirectoryPath);
            return new File[] {};
        }
        return jarFiles;
    }

    /**
     * 从 JAR 包组装 PluginInstance
     * @param jarFile JAR 文件
     * @return PluginInstance 列表
     */
    public List<PluginInstance> assembleFromJar(File jarFile) throws Exception {
        // 为每个 jar 包单独创建插件类加载器，用于隔离加载 jar 包中的类
        try (PluginClassLoader pluginClassLoader = classLoaderFactory.create(jarFile)) {
            // 读取元数据
            List<PluginMetadata> metadataList = metadataParser.parseAll(jarFile);

            // 扫描插件类
            List<Class<?>> pluginClasses = scanPluginClasses(jarFile, pluginClassLoader);

            // 将 jar 包名以 "-" 进行分割，取第一部分作为插件模块名
            String pluginModuleName = jarFile.getName().split("-", 2)[0];

            // 组装插件实例
            List<PluginInstance> instances = new ArrayList<>();
            for (Class<?> pluginClass : pluginClasses) {
                PluginInstance instance = createPluginInstance(pluginClass, metadataList, pluginModuleName);
                instances.add(instance);
            }
            return instances;
        }
    }

    /**
     * 扫描插件类
     * 遍历 JAR 包中的所有 .class 文件，找出符合插件规范的类
     * @param jarFile JAR 文件
     * @param classLoader 类加载器
     * @return 插件类列表
     * @throws Exception 扫描过程中发生的异常
     */
    private List<Class<?>> scanPluginClasses(File jarFile, PluginClassLoader classLoader) throws Exception {
        List<Class<?>> pluginClasses = new ArrayList<>();
        try (JarFile jar = new JarFile(jarFile)) {
            // 遍历 JAR 包中的所有文件
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                // 检查是否为 .class 文件
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .substring(0, entry.getName().length() - 6)  // 移除 .class 后缀
                            .replace('/', '.');  // 将路径分割符替换为 .

                    // 加载类并检查是否为插件
                    Class<?> clazz = classLoader.loadClass(className);
                    if (isPluginClass(clazz)) {
                        pluginClasses.add(clazz);
                    }
                }
            }
        }
        return pluginClasses;
    }

    /**
     * 检查类是否为插件
     * @param clazz  类
     * @return 是否为插件
     */
    private boolean isPluginClass(Class<?> clazz) {
        return YuniPlugin.class.isAssignableFrom(clazz) &&  // 继承 YuniPlugin 接口
                !clazz.isInterface() &&  // 不是接口
                !Modifier.isAbstract(clazz.getModifiers());  // 不是抽象类
    }

    /**
     * 创建插件实例
     * @param pluginClass  插件类
     * @param metadata  元数据类
     * @return  插件实例
     * @throws Exception  异常
     */
    private PluginInstance createPluginInstance(Class<?> pluginClass, PluginMetadata metadata) throws Exception {
        // 实例化插件
        YuniPlugin plugin = (YuniPlugin) pluginClass.getDeclaredConstructor().newInstance();

        if (plugin instanceof ScheduledPlugin) {
            return createActivePluginInstance((ScheduledPlugin) plugin, metadata);
        } else if (plugin instanceof PassivePlugin) {
            return createPassivePluginInstance((PassivePlugin<?, ?>) plugin, metadata);
        } else {
            throw new IllegalArgumentException("Unknown plugin type: " + pluginClass.getName());
        }
    }

    /**
     * 创建插件实例
     *
     * @param pluginClass      插件类
     * @param metadataList     元数据类列表
     * @param pluginModuleName 插件模块名
     * @return 插件实例
     * @throws Exception 异常
     */
    private PluginInstance createPluginInstance(Class<?> pluginClass, List<PluginMetadata> metadataList, String pluginModuleName) throws Exception {
        String pluginClassName = pluginClass.getName();
        for (PluginMetadata pluginMetadata : metadataList) {
            if (pluginClassName.equals(pluginMetadata.getId())) {
                pluginMetadata.setId(pluginModuleName + "-" + pluginMetadata.getId());
                return createPluginInstance(pluginClass, pluginMetadata);
            }
        }
        throw new RuntimeException("Plugin " + pluginClassName + " 没有找到与之对应的元数据配置！");
    }

    /**
     * 创建定时任务插件实例
     * @param scheduledPlugin 定时任务插件原始类
     * @param metadata  元数据类
     * @return  定时任务插件实例
     */
    private ScheduledPluginInstance createActivePluginInstance(ScheduledPlugin scheduledPlugin,
                                                               PluginMetadata metadata) {
        ScheduledPluginInstance instance = new ScheduledPluginInstance();
        instance.setScheduledPlugin(scheduledPlugin);
        instance.setPluginMetadata(metadata);
        instance.setCronExpression(scheduledPlugin.cronExpression());
        instance.setAction(scheduledPlugin.getAction());
        return instance;
    }

    /**
     * 创建被动插件实例
     * @param passivePlugin  被动插件原始类
     * @param metadata  元数据类
     * @return  被动插件实例
     */
    private PassivePluginInstance createPassivePluginInstance(PassivePlugin<?, ?> passivePlugin,
                                                              PluginMetadata metadata) {
        PassivePluginInstance instance = new PassivePluginInstance();
        instance.setPassivePlugin(passivePlugin);
        instance.setPluginMetadata(metadata);

        // 提取探测器
        YuniEventDetector<?> detector = passivePlugin.getDetector();
        instance.setDetector(detector);

        // 提取权限
        instance.setPermission(passivePlugin.pluginPermission());

        // 提取执行方法
        try {
            Method executeMethod = passivePlugin.getClass().getMethod("execute", SpringYuniEvent.class);
            instance.setExecuteMethod(executeMethod);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Plugin does not have execute method", e);
        }

        return instance;
    }
}

