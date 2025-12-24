package com.yuier.yuni.plugin.model.active.scheduled;

import com.yuier.yuni.plugin.model.active.ActivePlugin;

/**
 * @Title: ScheduledPlugin
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model
 * @Date 2025/12/22 21:48
 * @description: 主动触发的插件
 */

public interface ScheduledPlugin extends ActivePlugin {

    /**
     * 获取 cron 表达式。提供了 CronExpressionBuilder 工具类用于辅助生成 Cron 表达式
     * @return cron 表达式
     */
    String cronExpression();
}
