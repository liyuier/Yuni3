package com.yuier.yuni.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Title: SchedulerConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/23 23:19
 * @description: 定时任务配置类
 */

@Configuration
@EnableScheduling // 这个还是需要的（即使不用 @Scheduled）
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20); // 根据任务数量调整
        scheduler.setThreadNamePrefix("dynamic-task-");
        return scheduler;
    }
}