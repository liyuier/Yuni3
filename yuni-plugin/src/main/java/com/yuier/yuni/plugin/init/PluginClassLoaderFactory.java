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
     * 创建插件类加载器
     */
    public PluginClassLoader create(File jarFile) throws MalformedURLException {
        URL jarUrl = null;
        try {
            jarUrl = jarFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new PluginClassLoader(new URL[]{jarUrl},
                Thread.currentThread().getContextClassLoader());
    }
}

