package com.yuier.yuni.plugin.init;

/**
 * @Title: PluginClassLoader
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 21:59
 * @description: 插件加载器
 */

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件类加载器，用于隔离插件依赖
 */
public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 检查是否为系统类或框架类，优先使用父类加载器
        if (isSystemClass(name)) {
            return super.loadClass(name, resolve);
        }

        // 优先从当前加载器加载插件类
        synchronized (getClassLoadingLock(name)) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                try {
                    loadedClass = findClass(name);
                } catch (ClassNotFoundException e) {
                    // 委托给父类加载器
                    loadedClass = super.loadClass(name, resolve);
                }
            }

            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
    }

    /**
     * 判断是否为系统类
     */
    private boolean isSystemClass(String name) {
        return name.startsWith("java.") ||
                name.startsWith("javax.") ||
                name.startsWith("com.yuier.yuni.core") ||  // 框架核心类
                name.startsWith("com.yuier.yuni.plugin");  // 插件系统类
    }
}

