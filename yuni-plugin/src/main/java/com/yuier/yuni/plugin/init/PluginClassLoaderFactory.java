package com.yuier.yuni.plugin.init;

/**
 * @Title: PluginClassLoaderFactory
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 21:59
 * @description: 插件工厂
 */

import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 插件类加载器工厂
 */
@Component
public class PluginClassLoaderFactory {

    /**
     * 创建插件类加载器，为每个 jar 包单独创建
     * @param jarFile JAR 文件
     */
    public PluginClassLoader create(File jarFile) throws MalformedURLException {
        URL jarUrl = null;
        try {
            // 将 jar 包转换为 URL ，供类加载器加载
            jarUrl = jarFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new PluginClassLoader(new URL[]{jarUrl},  // 指定 jar 包
                Thread.currentThread().getContextClassLoader());  // 指定父类加载器，使用当前线程的上下文类加载器，确保可以访问到 Spring 容器中的类
    }
}

