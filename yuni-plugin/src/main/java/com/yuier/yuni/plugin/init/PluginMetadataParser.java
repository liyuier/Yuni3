package com.yuier.yuni.plugin.init;

/**
 * @Title: PluginMetadataParser
 * @Author yuier
 * @Package com.yuier.yuni.plugin.init
 * @Date 2025/12/23 22:01
 * @description: 插件元数据解析器
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.plugin.model.PluginMetadata;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件元数据解析器
 */
@Component
public class PluginMetadataParser {

    private final ObjectMapper objectMapper;

    public PluginMetadataParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 解析插件元数据
     * @param jarFile  包含插件的 jar 包
     * @return  插件元数据
     * @throws Exception  异常
     */
    public PluginMetadata parse(File jarFile) throws Exception {
        // 使用 try-with-resources 确保 JAR 文件被正确关闭
        try (JarFile jar = new JarFile(jarFile)) {
            // 从 JAR 文件中获取元数据文件
            JarEntry metadataEntry = (JarEntry) jar.getEntry("metadata.json");
            if (metadataEntry == null) {
                throw new IllegalArgumentException("metadata.json not found in JAR: " + jarFile.getName());
            }

            // 读取元数据文件，反序列化为 PluginMetadata 对象
            try (InputStream is = jar.getInputStream(metadataEntry)) {
                return objectMapper.readValue(is, PluginMetadata.class);
            }
        }
    }

    /**
     * 批量解析插件元数据
     * @param jarFile  包含插件的 jar 包
     * @return  插件元数据列表
     * @throws Exception  异常
     */
    public List<PluginMetadata> parseAll(File jarFile) throws Exception {
        // 使用 try-with-resources 确保 JAR 文件被正确关闭
        try (JarFile jar = new JarFile(jarFile)) {
            // 从 JAR 文件中获取元数据文件
            JarEntry metadataEntry = (JarEntry) jar.getEntry("metadata.json");
            if (metadataEntry == null) {
                throw new IllegalArgumentException("metadata.json not found in JAR: " + jarFile.getName());
            }

            // 读取元数据文件，反序列化为 PluginMetadata 对象
            try (InputStream is = jar.getInputStream(metadataEntry)) {
                try {
                    PluginMetadata[] dataArr = objectMapper.readValue(is, PluginMetadata[].class);
                    return List.of(dataArr);
                } catch (Exception e) {
                    // 如果无法解析为数组，尝试解析为单个对象
                    is.close();  // 关闭当前流
                    try (InputStream is2 = jar.getInputStream(metadataEntry)) {  // 重新打开流  /* 圈复杂度爆表 */
                        PluginMetadata singleResult = objectMapper.readValue(is2, PluginMetadata.class);
                        return List.of(singleResult);
                    }
                }
            }
        }
    }
}

