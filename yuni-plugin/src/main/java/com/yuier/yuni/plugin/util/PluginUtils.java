package com.yuier.yuni.plugin.util;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.bot.Bot;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.util.OneBotDeserializer;
import com.yuier.yuni.core.util.OneBotSerialization;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.plugin.manage.PluginManager;
import com.yuier.yuni.plugin.manage.PluginRegisterProcessor;
import com.yuier.yuni.plugin.model.YuniPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.jar.JarFile;

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

    /**
     * 向 SpringBoot 容器中动态注册 bean
     * @param bean 实例
     * @param <T> bean 的类型
     */
    public static <T> void registerBeanToSpring(T bean) {
        String beanName = StringUtils.uncapitalize(bean.getClass().getSimpleName());
        registerBeanToSpring(bean, beanName);
    }

    public static <T> void registerBeanToSpring(T bean, String beanName) {

        // 获取 Spring 上下文
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        // 强转为 ConfigurableApplicationContext ，获取更高级 BeanFactory 操作能力
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
        // 获取 BeanFactory
        AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
        // 注入依赖
        factory.autowireBean(bean);
        // 初始化 Bean
        factory.initializeBean(bean, beanName);
        // 注册为单例
        ctx.getBeanFactory().registerSingleton(beanName, bean);
    }

    //  包装一下，方便使用
    public static <T> T getBean(Class<T> clazz) {
        return SpringContextUtil.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return SpringContextUtil.getBean(beanName);
    }

    /**
     * 加载配置文件为 bean
     * @param configFilePath 配置文件路径
     * @param clazz bean 的类型
     * @param <T> bean 的类型
     * @return bean
     */
    public static <T> T loadJsonConfigFromJar(String configFilePath, Class<T> clazz, YuniPlugin plugin) {
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
     * 加载配置文件为字符串
     * @param configFilePath 配置文件路径
     * @return 配置文件内容
     */
    private static String loadConfigJsonToString(String configFilePath, YuniPlugin plugin) {
        if (!isJsonFile(configFilePath)) {
            throw new RuntimeException("传入的文件路径不是 json 文件！");
        }

        return loadTextFromPluginJar(plugin, configFilePath);
    }

    /**
     * 获取插件 jar 包文件名
     * @return 插件所在的 jar 包文件名
     */
    public static String getPluginJarFileName(YuniPlugin plugin) {
        // 先根据 plugin 获取 plugin id
        PluginRegisterProcessor pluginRegisterProcessor = SpringContextUtil.getBean(PluginRegisterProcessor.class);
        String pluginId = pluginRegisterProcessor.mapToPluginId(plugin);
        // 再根据 plugin id 获取 plugin jar 包
        PluginManager pluginManager = SpringContextUtil.getBean(PluginManager.class);
        return pluginManager.getPluginInstanceById(pluginId).getJarFileName();
    }

    /**
     * 获取插件 jar 包在 SpringBoot app 中的路径
     * @param plugin 插件对象
     * @return  插件 jar 包在 SpringBoot app 中的路径
     */
    public static String getPluginJarAppPath(YuniPlugin plugin) {
        return getPluginManager().getPluginDirectoryPath() + "/" + getPluginJarFileName(plugin);
    }

    /**
     * 从 jar 包中读取文本文件内容
     * @param jarFile 插件 jar 包
     * @param filePath 文件路径
     * @return 文件内容
     */
    private static String readFileTextFromJar(JarFile jarFile, String filePath) {
        try {
            return new String(jarFile.getInputStream(jarFile.getEntry(filePath)).readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从插件 jar 包中加载文本文件内容字符串
     * @param plugin 插件
     * @param resourcePath 文件在 jar 包内路径
     * @return 配置文件内容
     */
    public static String loadTextFromPluginJar(YuniPlugin plugin, String resourcePath) {
        String text = "";
        try (JarFile jarFile = new JarFile(getPluginJarAppPath(plugin))) {
            text = readFileTextFromJar(jarFile, resourcePath);
        } catch (Exception e) {
            log.error("从插件 jar 包中加载文本文件内容字符串失败！请检查 jar 包内文件路径是否正确！");
            e.printStackTrace();
        }
        return text;
    }

    // 检查文件名是否为 json
    public static boolean isJsonFile(String fileName) {
        return fileName.endsWith(".json");
    }

    public static PluginManager getPluginManager() {
        return SpringContextUtil.getBean(PluginManager.class);
    }

    public static String getPluginId(YuniPlugin plugin) {
        PluginRegisterProcessor pluginRegisterProcessor = SpringContextUtil.getBean(PluginRegisterProcessor.class);
        return pluginRegisterProcessor.mapToPluginId(plugin);
    }

    public static <T> T serialize(String json, Class<T> clazz) {
        OneBotDeserializer deserialization = SpringContextUtil.getBean(OneBotDeserializer.class);
        try {
            return deserialization.deserialize(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String deserialize(Object obj) {
        OneBotSerialization serialization = SpringContextUtil.getBean(OneBotSerialization.class);
        try {
            return serialization.serialize( obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendGroupMessage(long groupId, MessageChain message) {
        getOneBotAdapter().sendGroupMessage(groupId, message);
    }

    public static void sendPrivateMessage(long userId, MessageChain message) {
        getOneBotAdapter().sendPrivateMessage(userId, message);
    }

    public static String getAppDatabaseUrl() {
        return "jdbc:sqlite:./" + getBotAppConfig().getSqliteDbFile();
    }
}
