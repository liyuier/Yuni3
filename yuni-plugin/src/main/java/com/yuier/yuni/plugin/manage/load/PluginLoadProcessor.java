package com.yuier.yuni.plugin.manage.load;

import com.yuier.yuni.plugin.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Title: PluginLoadProcessor
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage.load
 * @Date 2026/1/6 15:05
 * @description: 新插件组装器
 */

@Component
@Slf4j
public class PluginLoadProcessor {

    @Autowired
    PluginClassLoaderFactory classLoaderFactory;
    @Autowired
    PluginMetadataParser metadataParser;
    @Autowired
    PluginInstanceFactory instanceFactory;

    /**
     * 收集插件目录下的所有 JAR 包
     * @param path 插件目录路径
     * @return JAR 文件列表
     */
    public List<File> collectJarFilesFromPath(String path) {
        File pluginDir = new File(path);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", path);
            return new ArrayList<>();
        }

        // 使用 FilenameFilter 过滤 jar 包
        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            log.warn("插件目录为空: {}", path);
            return new ArrayList<>();
        }
        return List.of(jarFiles);
    }

    /**
     * 从 JAR 包中加载插件类
     * @param jarFile  JAR 包文件
     * @return 插件类列表
     */
    public List<Class<?>> loadPluginClassesFromJarFile(File jarFile) throws Exception {
        List<Class<?>> pluginClasses = new ArrayList<>();
        try (PluginClassLoader pluginClassLoader = classLoaderFactory.create(jarFile)) {  // 每个 jar 包都单独创建一个类加载器
            try (JarFile jar = new JarFile(jarFile)) {
                // 遍历 JAR 包中的所有文件
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    // 检查是否为 .class 文件
                    if (entry.getName().endsWith(".class")) {
                        String classFullName = entry.getName()
                                .substring(0, entry.getName().length() - 6)  // 移除 .class 后缀
                                .replace('/', '.');  // 将路径分割符替换为 .

                        // 加载类并检查是否为插件
                        Class<?> clazz = pluginClassLoader.loadClass(classFullName);
                        if (isPluginClass(clazz)) {
                            pluginClasses.add(clazz);
                        }
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
     * 解析插件元数据
     * @param jarFile  JAR 包文件
     * @return 插件元数据
     */
    public PluginModuleMetadata parseModuleMetadata(File jarFile) throws Exception {
        return metadataParser.parseModuleMetadata(jarFile);
    }

    /**
     * 组装插件模块
     * @param jarFile  JAR 包文件
     * @return 插件模块实例
     */
    public PluginModuleInstance assemblePluginModule(File jarFile) throws Exception {
        PluginModuleInstance pluginModuleInstance = new PluginModuleInstance();
        pluginModuleInstance.setJarFileName(jarFile.getName());
        pluginModuleInstance.setPluginModuleMetadata(parseModuleMetadata(jarFile));
        return pluginModuleInstance;
    }

    /**
     * 组装插件实例
     * @param pluginModuleInstance  插件模块实例
     * @param pluginClasses  插件类列表
     * @return 插件实例列表
     */
    public List<PluginInstance> assemblePluginInstances(PluginModuleInstance pluginModuleInstance, List<Class<?>> pluginClasses) {
        ArrayList<PluginInstance> pluginInstances = new ArrayList<>();
        for (PluginMetadata pluginMetadata : pluginModuleInstance.getPluginMetadataList()) {
            for (Class<?> pluginClass : pluginClasses) {
                // 以插件类全限定名匹配元数据中的 id
                if (pluginClass.getName().equals(pluginMetadata.getId())) {
                    pluginMetadataPreProcess(pluginModuleInstance.getPluginModuleMetadata(), pluginMetadata);
                    PluginInstance pluginInstance = null;
                    try {
                        pluginInstance = instanceFactory.createPluginInstance(pluginMetadata, pluginClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (pluginInstance != null) {
                        pluginInstance.initialize();
                        pluginInstances.add(pluginInstance);
                    } else {
                        log.warn("插件实例创建失败: {}", pluginClass.getName());
                    }
                    break;
                }
            }
        }
        return pluginInstances;
    }

    /**
     * 插件元数据预处理
     * @param pluginModuleMetadata  插件模块元数据
     * @param pluginMetadata  插件元数据
     */
    public void pluginMetadataPreProcess(PluginModuleMetadata pluginModuleMetadata, PluginMetadata pluginMetadata) {
        // 设置插件全名称，使用 模块ID-插件ID 的方式拼接
        pluginMetadata.setFullId(pluginModuleMetadata.getModuleId() + "-" + pluginMetadata.getId());
        // 设置插件所属的模块ID
        pluginMetadata.setModuleId(pluginModuleMetadata.getModuleId());
        // 插件所属的模块名
        pluginMetadata.setModuleName(pluginModuleMetadata.getModuleName());
        // 如果没有填写插件使用提示，则使用插件描述
        if (null == pluginMetadata.getTips() || pluginMetadata.getTips().isEmpty()) {
            pluginMetadata.setTips(List.of(pluginMetadata.getDescription()));
        }
    }
}
