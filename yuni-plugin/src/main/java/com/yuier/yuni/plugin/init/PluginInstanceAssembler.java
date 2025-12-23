package com.yuier.yuni.plugin.init;

/**
 * @Title: PluginInstanceAssembler
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 21:57
 * @description: 插件组装器
 */

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.event.model.context.SpringYuniEvent;
import com.yuier.yuni.event.model.message.detector.YuniEventDetector;
import com.yuier.yuni.event.model.message.detector.command.CommandDetector;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModel;
import com.yuier.yuni.plugin.model.PluginInstance;
import com.yuier.yuni.plugin.model.PluginMetadata;
import com.yuier.yuni.plugin.model.YuniPlugin;
import com.yuier.yuni.plugin.model.active.ScheduledPlugin;
import com.yuier.yuni.plugin.model.active.ScheduledPluginInstance;
import com.yuier.yuni.plugin.model.passive.PassivePlugin;
import com.yuier.yuni.plugin.model.passive.PassivePluginInstance;
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
public class PluginInstanceAssembler {

    private final PluginClassLoaderFactory classLoaderFactory;
    private final PluginMetadataParser metadataParser;

    public PluginInstanceAssembler(PluginClassLoaderFactory classLoaderFactory,
                                   PluginMetadataParser metadataParser) {
        this.classLoaderFactory = classLoaderFactory;
        this.metadataParser = metadataParser;
    }

    /**
     * 从 JAR 包组装 PluginInstance
     * @param jarFile JAR 文件
     * @return PluginInstance 列表
     */
    public List<PluginInstance> assembleFromJar(File jarFile) throws Exception {
        // 创建插件类加载器
        try (PluginClassLoader pluginClassLoader = classLoaderFactory.create(jarFile)) {
            // 读取元数据
            PluginMetadata metadata = metadataParser.parse(jarFile);

            // 扫描插件类
            List<Class<?>> pluginClasses = scanPluginClasses(jarFile, pluginClassLoader);

            // 组装插件实例
            List<PluginInstance> instances = new ArrayList<>();
            for (Class<?> pluginClass : pluginClasses) {
                PluginInstance instance = createPluginInstance(pluginClass, metadata, pluginClassLoader);
                instances.add(instance);
            }
            return instances;
        }
    }

    /**
     * 扫描 JAR 包中的插件类
     */
    private List<Class<?>> scanPluginClasses(File jarFile, PluginClassLoader classLoader) throws Exception {
        List<Class<?>> pluginClasses = new ArrayList<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .substring(0, entry.getName().length() - 6) // 移除 .class 后缀
                            .replace('/', '.');

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
     * 判断是否为插件类
     */
    private boolean isPluginClass(Class<?> clazz) {
        return YuniPlugin.class.isAssignableFrom(clazz) &&
                !clazz.isInterface() &&
                !Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 创建插件实例
     */
    private PluginInstance createPluginInstance(Class<?> pluginClass,
                                                PluginMetadata metadata,
                                                PluginClassLoader classLoader) throws Exception {
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
     * 创建主动插件实例
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
     */
    private PassivePluginInstance createPassivePluginInstance(PassivePlugin<?, ?> passivePlugin,
                                                              PluginMetadata metadata) {
        PassivePluginInstance instance = new PassivePluginInstance();
        instance.setPassivePlugin(passivePlugin);
        instance.setPluginMetadata(metadata);

        // 提取探测器
        YuniEventDetector<?> detector = passivePlugin.getDetector();
        instance.setDetector(detector);

        // 提取权限（如果有命令探测器）
        if (detector instanceof CommandDetector) {
            CommandModel commandModel = ((CommandDetector) detector).getCommandModel();
            instance.setPermission(commandModel.getPermission());
        } else {
            // 默认权限
            instance.setPermission(UserPermission.USER);
        }

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

