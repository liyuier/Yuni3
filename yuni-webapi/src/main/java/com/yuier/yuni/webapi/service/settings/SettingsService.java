package com.yuier.yuni.webapi.service.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * 系统设置服务。
 * 直接读取 application.yml 文件内容，返回结构化配置树。
 */
@Slf4j
@Service
public class SettingsService {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * 获取 application.yml 配置内容（已脱敏）。
     * @return 嵌套的配置树
     */
    public Map<String, Object> getConfig() {
        Map<String, Object> merged = new LinkedHashMap<>();
        merged.putAll(loadYaml("application.yml"));

        // 如果有激活的 profile，合并对应配置文件
        if (activeProfile != null && !activeProfile.isBlank()) {
            for (String profile : activeProfile.split(",")) {
                String profileFile = "application-" + profile.trim() + ".yml";
                try {
                    merged.putAll(loadYaml(profileFile));
                } catch (Exception e) {
                    log.debug("无 profile 配置文件: {}", profileFile);
                }
            }
        }

        maskSensitive(merged);
        return merged;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadYaml(String filename) {
        try (InputStream in = new ClassPathResource(filename).getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            return data != null ? data : Map.of();
        } catch (Exception e) {
            log.debug("读取配置文件失败: {}", filename, e);
            return Map.of();
        }
    }

    /**
     * 递归脱敏 password / token / secret 字段。
     */
    @SuppressWarnings("unchecked")
    private void maskSensitive(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                maskSensitive((Map<String, Object>) value);
            } else if (value instanceof String str) {
                if (key.contains("password") || key.contains("token") || key.contains("secret")) {
                    entry.setValue(mask(str));
                }
            }
        }
    }

    private String mask(String value) {
        if (value == null || value.length() <= 4) return "****";
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }
}
