package com.yuier.yuni.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: PluginDisableEvent
 * @Author yuier
 * @Package com.yuier.yuni.plugin.event
 * @Date 2025/12/27 20:46
 * @description:
 */

@Data
@AllArgsConstructor
public class PluginDisableEvent {

    private Long groupId;

    private Long userId;
}
