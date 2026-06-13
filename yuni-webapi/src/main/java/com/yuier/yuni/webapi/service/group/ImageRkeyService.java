package com.yuier.yuni.webapi.service.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * QQ 图片 URL 的 rkey 参数刷新服务。
 * 当图片 URL 中的 rkey 过期导致图片无法加载时，从 rkey 服务获取新的 group_rkey 替换。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageRkeyService {

    private final ObjectMapper objectMapper;

    private static final String RKEY_SERVICE_URL = "https://llob.linyuchen.net/rkey";

    /** 缓存刷新后的 rkey，避免同一批次消息重复请求 */
    private volatile String cachedRkey;
    private volatile long cachedAt;

    /**
     * 刷新图片 URL 中过期的 rkey 参数。
     * @param url 原始图片 URL
     * @return 刷新后的 URL，失败时返回原 URL
     */
    public String refreshImageUrl(String url) {
        if (url == null || !url.contains("rkey=")) {
            return url;
        }
        try {
            String newRkey = getRkey();
            if (newRkey == null) return url;
            return url.replaceAll("rkey=[^&]*", "rkey=" + newRkey);
        } catch (Exception e) {
            log.debug("刷新 rkey 失败: {}", e.getMessage());
            return url;
        }
    }

    /**
     * 递归刷新消息段中所有图片的 URL。
     */
    @SuppressWarnings("unchecked")
    public void refreshSegments(List<Map<String, Object>> segments) {
        if (segments == null) return;
        for (Map<String, Object> seg : segments) {
            if (!"image".equals(seg.get("type"))) continue;
            Map<String, Object> data = (Map<String, Object>) seg.get("data");
            if (data == null) continue;
            Object urlObj = data.get("url");
            if (urlObj instanceof String url) {
                data.put("url", refreshImageUrl(url));
            }
        }
    }

    /**
     * 获取新的 group_rkey，带简单内存缓存（60 秒有效）。
     */
    private String getRkey() {
        if (cachedRkey != null && System.currentTimeMillis() - cachedAt < 60_000) {
            return cachedRkey;
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(RKEY_SERVICE_URL).toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            if (conn.getResponseCode() != 200) return null;

            JsonNode root = objectMapper.readTree(conn.getInputStream());
            String rkey = root.path("group_rkey").asText(null);
            if (rkey != null && !rkey.isEmpty()) {
                cachedRkey = rkey;
                cachedAt = System.currentTimeMillis();
                return rkey;
            }
            return null;
        } catch (Exception e) {
            log.debug("获取 rkey 失败: {}", e.getMessage());
            return null;
        }
    }
}
