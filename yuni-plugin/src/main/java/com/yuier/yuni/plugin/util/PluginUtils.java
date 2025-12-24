package com.yuier.yuni.plugin.util;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.core.model.bot.BotModel;
import com.yuier.yuni.core.util.SpringContextUtil;

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

}
