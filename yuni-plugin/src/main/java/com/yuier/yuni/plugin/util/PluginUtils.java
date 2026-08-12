package com.yuier.yuni.plugin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuier.yuni.contact.manage.YuniContactManager;
import com.yuier.yuni.core.bot.MessageTarget;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.bot.BotGroupMemberInfo;
import com.yuier.yuni.core.model.bot.Bot;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.bot.JsonCodec;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.data.CrudRepository;
import com.yuier.yuni.plugin.data.DeleteBuilder;
import com.yuier.yuni.plugin.data.PluginDataService;
import com.yuier.yuni.plugin.data.QueryBuilder;
import com.yuier.yuni.plugin.data.UpdateBuilder;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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
     * 获取 YuniBot 实例
     * @return YuniBot
     */
    public static YuniBot getYuniBot() {
        return SpringContextUtil.getBean(YuniBot.class);
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

    public static String getPluginRootPath(Class<? extends YuniPlugin> pluginClazz) {
        // 先根据 plugin 获取 plugin id
        PluginContainer container = SpringContextUtil.getBean(PluginContainer.class);
        String pluginFullId = container.getPluginFullIdByPluginClass(pluginClazz);
        // 再根据插件 ID 获取模块
        PluginModuleInstance moduleByPluginFullId = container.getPluginModuleByPluginFullId(pluginFullId);
        return moduleByPluginFullId.getJarFileParentPath();
    }

    public static <T> T loadJsonConfigFromPlugin(String configFilePath, Class<T> clazz, Class<? extends YuniPlugin> pluginClazz) {
        if (!isJsonFile(configFilePath)) {
            throw new RuntimeException("传入的文件路径不是 json 文件！");
        }

        String json = loadConfigJsonToString(configFilePath, pluginClazz);
        JsonCodec jsonCodec = SpringContextUtil.getBean(JsonCodec.class);
        return jsonCodec.fromJson(json, clazz);
    }

    private static String loadConfigJsonToString(String configFilePath, Class<? extends YuniPlugin> pluginClazz) {
        if (!isJsonFile(configFilePath)) {
            throw new RuntimeException("传入的文件路径不是 json 文件！");
        }

        return loadTextFromPluginFolder(configFilePath, pluginClazz);
    }

    public static String loadTextFromPluginFolder(String resourcePath, Class<? extends YuniPlugin> pluginClazz) {
        String text = "";
        try {
            text = Files.readString(Paths.get(getPluginRootPath(pluginClazz), resourcePath));
        } catch (Exception e) {
            log.error("从插件包中加载文本文件内容字符串失败！请检查插件目录下文件路径是否正确！");
            e.printStackTrace();
        }
        return text;
    }

    public static Font loadFontFromPlugin(String fontFilePath, int fontSize, Class<? extends YuniPlugin> pluginClazz) {
        Font font = null;
        try {
            byte[] fontData = Files.readAllBytes(Paths.get(getPluginRootPath(pluginClazz)).resolve(fontFilePath)); // 读取字节
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

    // 反序列化
    public static <T> T deserialize(String json, Class<T> clazz) {
        return SpringContextUtil.getBean(JsonCodec.class).fromJson(json, clazz);
    }

    // 序列化
    public static String serialize(Object obj) {
        return SpringContextUtil.getBean(JsonCodec.class).toJson(obj);
    }

    // 发送群消息
    public static void sendGroupMessage(long groupId, MessageChain message) {
        getYuniBot().sendMessage(MessageTarget.group(groupId), message);
    }

    // 发送私聊消息
    public static void sendPrivateMessage(long userId, MessageChain message) {
        getYuniBot().sendMessage(MessageTarget.privateChat(userId), message);
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
        BotGroupMemberInfo groupMemberInfo = getYuniBot().getGroupMemberInfo(String.valueOf(groupId), String.valueOf(userId)).orElse(null);
        if (groupMemberInfo == null) return String.valueOf(userId);
        return groupMemberInfo.getCard() != null && !groupMemberInfo.getCard().isEmpty() ? groupMemberInfo.getCard() : groupMemberInfo.getNickname();
    }

    /**
     * 从列表中随机挑选一个元素
     * @param list 列表
     * @return 随机元素
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("列表为空！");
        }

        int index = ThreadLocalRandom.current().nextInt(list.size());

        return list.get(index);
    }

    /**
     * 检查概率是否命中
     * @param rate 概率
     * @return 是否命中
     */
    public static Boolean checkHitProbability(float rate) {
        if (rate < 0.0f || rate > 1.0f) {
            throw new IllegalArgumentException(
                    String.format("概率 rate 必须在 [0.0f, 1.0f] 范围内。当前值: %f", rate)
            );
        }

        float randomValue = new Random().nextFloat();
        log.debug("[CrazyThursdayUtil] 随机数：{}, rate: {}", randomValue, rate);
        return randomValue < rate;
    }

    // 根据用户 ID 查找用户所在的群组
    public static HashSet<Long> findUserGroups(Long userId) {
        YuniContactManager contactManager = PluginUtils.getBean(YuniContactManager.class);
        return contactManager.findUserGroupIdSet(userId);
    }

    // ==================== 数据库访问 ====================

    /**
     * 注册实体类并自动建表。
     * 建议在插件的 {@code initialize()} 中调用。
     *
     * <pre>{@code
     * public void initialize() {
     *     PluginUtils.registerEntity(UserSteamId.class);
     * }
     * }</pre>
     */
    public static void registerEntity(Class<?> entityClass) {
        PluginDataService.getInstance().registerEntity(entityClass);
    }

    /**
     * 获取实体的通用 CRUD 仓储。
     *
     * <pre>{@code
     * PluginUtils.repo(UserSteamId.class).save(entity);
     * PluginUtils.repo(UserSteamId.class).findByField("userId", 123L);
     * }</pre>
     */
    public static <T> CrudRepository<T> repo(Class<T> entityClass) {
        return PluginDataService.getInstance().getRepository(entityClass);
    }

    /**
     * 获取实体的链式查询构造器。
     *
     * <pre>{@code
     * List<UserSteamId> result = PluginUtils.query(UserSteamId.class)
     *     .where("userId", 123L)
     *     .orderBy("id", "DESC")
     *     .limit(10)
     *     .list();
     * }</pre>
     */
    public static <T> QueryBuilder<T> query(Class<T> entityClass) {
        return PluginDataService.getInstance().createQueryBuilder(entityClass);
    }

    /**
     * 获取实体的链式删除构造器。
     *
     * <pre>{@code
     * int rows = PluginUtils.delete(UserSteamId.class)
     *     .where("userId", 123L)
     *     .where("steamId", "XXX")
     *     .execute();
     * }</pre>
     */
    public static <T> DeleteBuilder<T> delete(Class<T> entityClass) {
        return PluginDataService.getInstance().createDeleteBuilder(entityClass);
    }

    /**
     * 获取实体的链式更新构造器。
     *
     * <pre>{@code
     * int rows = PluginUtils.update(UserSteamId.class)
     *     .set("steamId", "NEW_ID")
     *     .where("userId", 123L)
     *     .execute();
     * }</pre>
     */
    public static <T> UpdateBuilder<T> update(Class<T> entityClass) {
        return PluginDataService.getInstance().createUpdateBuilder(entityClass);
    }

    /**
     * 获取数据库访问入口，用于执行原始 SQL。
     *
     * <pre>{@code
     * PluginUtils.db().rawQuery("SELECT * FROM t WHERE x = :v", Map.of("v", 1), MyPojo.class);
     * PluginUtils.db().rawUpdate("DELETE FROM t WHERE x = :v", Map.of("v", 1));
     * }</pre>
     */
    public static PluginDataService db() {
        return PluginDataService.getInstance();
    }
}
