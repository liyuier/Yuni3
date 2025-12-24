package com.yuier.yuni.plugin.util;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.core.model.bot.BotModel;
import com.yuier.yuni.core.util.SpringContextUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * @Title: PluginUtils
 * @Author yuier
 * @Package com.yuier.yuni.plugin.util
 * @Date 2025/12/24 3:49
 * @description: 插件编写相关工具类
 */

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
    public static BotModel getBotModelConfig() {
        return SpringContextUtil.getBean(BotModel.class);
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
    public static <T> void registerBeanUtil(T bean) {
        String beanName = StringUtils.uncapitalize(bean.getClass().getSimpleName());
        registerBeanUtil(bean, beanName);
    }

    public static <T> void registerBeanUtil(T bean, String beanName) {

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
}
