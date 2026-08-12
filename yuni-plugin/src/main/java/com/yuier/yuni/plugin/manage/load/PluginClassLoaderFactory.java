package com.yuier.yuni.plugin.manage.load;

/**
 * @Title: PluginClassLoaderFactory
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 21:59
 * @description: 插件工厂
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 插件类加载器工厂
 */
@Slf4j
@Component
public class PluginClassLoaderFactory {

    /**
     * 创建插件类加载器，为每个 jar 包单独创建
     * @param jarFile JAR 文件
     */
    public PluginClassLoader create(File jarFile) {
        URL jarUrl = null;
        try {
            // 将 jar 包转换为 URL ，供 URLClassLoader 类加载器加载
            jarUrl = jarFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        // 父加载器必须使用加载本工厂类的加载器（fat jar 下为 LaunchedClassLoader），
        // 确保能看到框架类（BOOT-INF/classes）。
        // 不能使用 Thread.currentThread().getContextClassLoader()：
        // commonPool 工作线程的上下文类加载器是 AppClassLoader，看不到框架类，
        // 插件类加载时会抛 NoClassDefFoundError，导致重载静默失败
        return new PluginClassLoader(new URL[]{jarUrl},  // 指定 jar 包
                PluginClassLoaderFactory.class.getClassLoader());
    }
}

