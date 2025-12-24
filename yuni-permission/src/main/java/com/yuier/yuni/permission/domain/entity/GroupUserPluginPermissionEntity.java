package com.yuier.yuni.permission.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.yuier.yuni.core.enums.UserPermission;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (GroupUserPluginPermission)表实体类
 *
 * @author liyuier
 * @since 2025-12-24 15:49:00
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("group_user_plugin_permission")
public class GroupUserPluginPermissionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long groupId;

    private String pluginId;

    private Long userId;

    @EnumValue
    private UserPermission permissionLevel;
}
