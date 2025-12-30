package com.yuier.yuni.core.task;

import com.yuier.yuni.core.util.LogStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Title: DynamicTaskManager
 * @Author yuier
 * @Package com.yuier.yuni.core.task
 * @Date 2025/12/23 23:17
 * @description: 动态管理定时任务
 */

@Component
@Slf4j
public class DynamicTaskManager {

    // Spring 提供的线程安全调度器，自动注入
    @Autowired
    private TaskScheduler taskScheduler;

    // 保存任务引用，方便后续取消（key 可以是 pluginId）
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    /**
     * 动态添加一个 cron 定时任务
     *
     * @param taskId        任务唯一 ID（如 pluginId），用于后续取消
     * @param cronExpression cron 表达式，如 "0/10 * * * * ?"
     * @param task          要执行的逻辑（Runnable）
     */
    public void addCronTask(String taskId, String cronExpression, Runnable task) {
        // 防止重复注册
        if (tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("任务已存在: " + taskId);
        }

        // 创建触发器
        CronTrigger trigger = new CronTrigger(cronExpression);

        // 注册任务，并保存返回的 future
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                CompletableFuture.runAsync(() -> {
                    log.info("定时任务 [" + taskId + "] 正在执行...");
                    task.run();
                });
            } catch (Exception e) {
                // 记录异常，避免静默失败
                log.info("任务 [" + taskId + "] 执行出错: " + e.getMessage());
            }
        }, trigger);

        tasks.put(taskId, future);
        log.info("已注册定时任务: " + taskId + " | cron = " + LogStringUtil.buildBrightRedLog(cronExpression));
    }

    /**
     * 取消指定任务
     */
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        if (future != null) {
            future.cancel(true); // true = 中断正在执行的任务
            log.info("❌ 已取消任务: " + taskId);
        }
    }
}
