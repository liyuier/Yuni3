package com.yuier.yuni.webapi.dto.plugin;

import lombok.Data;

/**
 * @Title: PluginActionReq
 * @Author yuier
 * @Package com.yuier.yuni.webapi.dto
 * @Date 2026/06/13
 * @description: 插件启停/重载请求
 */

@Data
public class PluginActionReq {
    private String pluginId;
    /** 为空时全局操作，非空时针对指定群组 */
    private Long groupId;
}
