package com.yuier.yuni.plugin.model.active.scheduled;

import com.yuier.yuni.plugin.model.active.ActivePluginInstance;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: ScheduledPluginInstance
 * @Author yuier
 * @Package com.yuier.yuni.plugin.model.active
 * @Date 2025/12/23 21:08
 * @description: 主动插件实体类
 */

@Data
@NoArgsConstructor
public class ScheduledPluginInstance extends ActivePluginInstance {

    private String cronExpression;
}
