package com.yuier.yuni.plugin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.api.group.GroupMemberInfo;
import com.yuier.yuni.core.model.bot.Bot;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.manage.PluginContainer;
import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.model.PluginModuleInstance;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Title: PluginUtils
 * @Author yuier
 * @Package com.yuier.yuni.plugin.util
 * @Date 2025/12/24 3:49
 * @description: 插件编写相关工具类
 */

@Slf4j
public class PluginUtils {

    /**
     * 获取 OneBotAdapter
     * @return OneBotAdapter
     */
    public static OneBotAdapter getOneBotAdapter() {
        return SpringContextUtil.getBean(OneBotAdapter.class);
    }

    /**
     * 获取 Bot 配置实体
     * @return Bot 配置实体
     */
    public static Bot getBotModelConfig() {
        return SpringContextUtil.getBean(Bot.class);
    }

    /**
     * 获取 Bot 业务相关配置实体
     * @return Bot 业务相关配置实体
     */
    public static BotApp getBotAppConfig() {
        return SpringContextUtil.getBean(BotApp.class);
    }

    /**
     * 获取 bot qq 号
     * @return bot qq 号
     */
    public static Long getBotId() {
        return getBotModelConfig().getId();
    }

    /**
     * 获取 bot 昵称
     * @return bot 昵称
     */
    public static String getBotNickName() {
        return getBotModelConfig().getNickName();
    }

    /**
     * 获取 bot 主人 qq 号
     * @return bot 主人 qq 号
     */
    public static Long getBotMasterId() {
        return getBotModelConfig().getMasterId();
    }

    /**
     * 获取 bot app 命令前缀
     * @return bot app 命令前缀
     */
    public static String getBotAppCommandFlag() {
        return getBotAppConfig().getCommandFlag();
    }

    //  包装一下，方便使用
    public static <T> T getBean(Class<T> clazz) {
        return SpringContextUtil.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return SpringContextUtil.getBean(beanName);
    }

    /**
     * 获取插件目录在 app 下的相对路径
     * @return 插件目录
     */
    public static String getPluginRootPath(YuniPlugin plugin) {
        // 先根据 plugin 获取 plugin id
        PluginContainer container = SpringContextUtil.getBean(PluginContainer.class);
        String pluginFullId = container.getPluginFullIdByPluginClass(plugin.getClass());
        // 再根据插件 ID 获取模块
        PluginModuleInstance moduleByPluginFullId = container.getPluginModuleByPluginFullId(pluginFullId);
        return moduleByPluginFullId.getJarFileParentPath();
    }

    /**
     * 加载配置 json 文件为 bean
     * @param configFilePath 配置文件路径
     * @param clazz bean 的类型
     * @param <T> bean 的类型
     * @return bean
     */
    public static <T> T loadJsonConfigFromPlugin(String configFilePath, Class<T> clazz, YuniPlugin plugin) {
        if (!isJsonFile(configFilePath)) {
            throw new RuntimeException("传入的文件路径不是 json 文件！");
        }

        String json = loadConfigJsonToString(configFilePath, plugin);
        OneBotDeserializer serialization = SpringContextUtil.getBean(OneBotDeserializer.class);
        try {
            return serialization.deserialize(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载配置 json 文件为字符串
     * @param configFilePath 配置文件路径
     * @return 配置文件内容
     */
    private static String loadConfigJsonToString(String configFilePath, YuniPlugin plugin) {
        if (!isJsonFile(configFilePath)) {
            throw new RuntimeException("传入的文件路径不是 json 文件！");
        }

        return loadTextFromPluginFolder(plugin, configFilePath);
    }

    /**
     * 从插件 jar 包中加载文本文件内容字符串
     * @param plugin 插件
     * @param resourcePath 文件在 jar 包内路径
     * @return 配置文件内容
     */
    public static String loadTextFromPluginFolder(YuniPlugin plugin, String resourcePath) {
        String text = "";
        try {
            text = Files.readString(Paths.get(getPluginRootPath(plugin), resourcePath));
        } catch (Exception e) {
            log.error("从插件包中加载文本文件内容字符串失败！请检查插件目录下文件路径是否正确！");
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 从插件 jar 包中加载字体文件
     * @param fontFilePath 字体文件路径
     * @param fontSize 字体大小
     * @return 字体对象
     */
    public static Font loadFontFromPlugin(YuniPlugin plugin, String fontFilePath, int fontSize) {
        Font font = null;
        try {
            byte[] fontData = Files.readAllBytes(Paths.get(getPluginRootPath(plugin)).resolve(fontFilePath)); // 读取字节
            try (InputStream is = new ByteArrayInputStream(fontData)) { // 包装为流
                font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont((float) fontSize); // 加载字体
            }
        } catch (Exception e) {
            log.error("从插件中加载字体文件失败");
            e.printStackTrace();
        }
        return font;
    }

    // 检查文件名是否为 json
    public static boolean isJsonFile(String fileName) {
        return fileName.endsWith(".json");
    }

    // 获取插件管理器
    public static PluginManager getPluginManager() {
        return SpringContextUtil.getBean(PluginManager.class);
    }

    // 序列化
    public static <T> T serialize(String json, Class<T> clazz) {
        OneBotDeserializer deserialization = SpringContextUtil.getBean(OneBotDeserializer.class);
        try {
            return deserialization.deserialize(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 反序列化
    public static String deserialize(Object obj) {
        OneBotSerialization serialization = SpringContextUtil.getBean(OneBotSerialization.class);
        try {
            return serialization.serialize( obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 发送群消息
    public static void sendGroupMessage(long groupId, MessageChain message) {
        getOneBotAdapter().sendGroupMessage(groupId, message);
    }

    // 发送私聊消息
    public static void sendPrivateMessage(long userId, MessageChain message) {
        getOneBotAdapter().sendPrivateMessage(userId, message);
    }

    // 获取数据库连接 URL
    public static String getAppDatabaseUrl() {
        return "jdbc:sqlite:./" + getBotAppConfig().getSqliteDbFile();
    }

    // 简单 Post 请求
    public static <T> T simplePost(String url, Map<String, Object> params, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        try {
            String bodyJson = response.getBody();
            return getBean(ObjectMapper.class).readValue(bodyJson, responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 简单 Get 请求
    public static <T> T simpleGet(String url, Map<String, String> params, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, params);
        try {
            String bodyJson = response.getBody();
            return getBean(ObjectMapper.class).readValue(bodyJson, responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 不带参数的 Get 请求
    public static <T> T simpleGet(String url, Class<T> responseType) {
        return simpleGet(url, new HashMap<>(), responseType);
    }

    public static String getGroupMemberName(long groupId, long userId) {
        GroupMemberInfo groupMemberInfo = getOneBotAdapter().getGroupMemberInfo(groupId, userId, true);
        return groupMemberInfo.getCard() != null && !groupMemberInfo.getCard().isEmpty() ? groupMemberInfo.getCard() : groupMemberInfo.getNickname();
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("列表为空！");
        }

        int index = ThreadLocalRandom.current().nextInt(list.size());

        return list.get(index);
    }
}
