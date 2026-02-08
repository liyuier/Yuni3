package com.yuier.yuni.plugin.manage.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.PluginModuleMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Title: PluginMetadataParser
 * @Author yuier
 * @Package com.yuier.yuni.plugin.manage.load
 * @Date 2026/1/6 15:14
 * @description:
 */

@Slf4j
@Component
public class PluginMetadataParser {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 解析插件元数据
     * @param pluginModuleInstance 插件模块实例
     * @return 插件元数据
     * @throws Exception 抛出异常
     */
    public PluginModuleMetadata parseModuleMetadata(PluginModuleInstance pluginModuleInstance) throws Exception {
        String pluginModulePath = Paths.get(pluginModuleInstance.getJarFileParentPath()).toString();
        String metaDataJson = Files.readString(Paths.get(pluginModulePath, "metadata.json"));
        try {
            return objectMapper.readValue(metaDataJson, PluginModuleMetadata.class);
        } catch (Exception e) {
            throw new Exception("插件模块 " + pluginModuleInstance.getJarFileName() + " 元数据配置有误，请参考 example 项目检查模块配置格式！", e);
        }
    }
}
