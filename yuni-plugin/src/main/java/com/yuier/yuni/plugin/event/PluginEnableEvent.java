package com.yuier.yuni.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: PluginEnableEvent
 * @Author yuier
 * @Package com.yuier.yuni.plugin.event
 * @Date 2025/12/27 20:38
 * @description:
 */

@Data
@AllArgsConstructor
public class PluginEnableEvent {

    private Long groupId;

    private Long userId;
}
