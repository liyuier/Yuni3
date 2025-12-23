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
     * 解析 JAR 包中的元数据
     */
    public PluginMetadata parse(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry metadataEntry = (JarEntry) jar.getEntry("metadata.json");
            if (metadataEntry == null) {
                throw new IllegalArgumentException("metadata.json not found in JAR: " + jarFile.getName());
            }

            try (InputStream is = jar.getInputStream(metadataEntry)) {
                return objectMapper.readValue(is, PluginMetadata.class);
            }
        }
    }
}

