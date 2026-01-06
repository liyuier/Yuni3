package com.yuier.yuni.plugin.manage.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.plugin.model.PluginModuleMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Title: PluginMetadataParser
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage.load
 * @Date 2026/1/6 15:14
 * @description:
 */

@Component
public class PluginMetadataParser {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 解析插件元数据
     * @param jarFile jar 文件
     * @return 插件元数据
     * @throws Exception 抛出异常
     */
    public PluginModuleMetadata parseModuleMetadata(File jarFile) throws Exception {
        // 使用 try-with-resources 确保 JAR 文件被正确关闭
        try (JarFile jar = new JarFile(jarFile)) {
            // 从 JAR 文件中获取元数据文件
            JarEntry metadataEntry = (JarEntry) jar.getEntry("metadata.json");
            if (metadataEntry == null) {
                throw new IllegalArgumentException("Jar包 " + jarFile.getName() + " 中未找到 metadata.json 文件");
            }

            // 读取元数据文件，反序列化为 PluginMetadata 对象
            try (InputStream is = jar.getInputStream(metadataEntry)) {
                try {
                    return objectMapper.readValue(is, PluginModuleMetadata.class);
                } catch (Exception e) {
                    throw new Exception("插件模块" + jar.getName() + "元数据配置有误，请参考 example 项目检查模块配置格式！", e);
                }
            }
        }
    }
}
