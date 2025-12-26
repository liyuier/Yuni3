package com.yuier.yuni.plugin.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (PluginCall)表实体类
 *
 * @author liyuier
 * @since 2025-12-27 03:02:10
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("plugin_call")
public class PluginCallEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long timeStamp;

    private String formatTime;

    private Long botId;

    private Long messageId;

    private String toLogStr;

    private Long senderId;

    private Long groupId;

    private String pluginModule;

    private String pluginId;

    private String pluginName;

    private String pluginDescription;

    private String pluginAuthor;

    // command、pattern 等
    private String pluginDetectorType;

    // pluginMetadata 里写的，提供的功能的类别。娱乐、学习、工具等
    private String pluginFunctionType;

    private String icon;

    private String homePage;

    private String sourceCodeAddress;
}
