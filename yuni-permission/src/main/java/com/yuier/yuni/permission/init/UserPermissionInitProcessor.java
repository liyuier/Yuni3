package com.yuier.yuni.permission.init;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.permission.domain.entity.GroupUserPluginPermissionEntity;
import com.yuier.yuni.permission.service.GroupUserPluginPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title: UserPermissionInitProcessor
 * @Author yuier
 * @Package com.yuier.yuni.permission.init
 * @Date 2025/12/24 15:58
 * @description: 权限管理器初始化
 */

@Component
public class UserPermissionInitProcessor {

    @Autowired
    GroupUserPluginPermissionService permissionService;

    public Map<String, UserPermission> loadUserPermissions() {
        // 获取所有用户权限
        List<GroupUserPluginPermissionEntity> list = permissionService.list();
        return list.stream().collect(
                Collectors.toMap(
                        (e) -> e.getUserId() + "@" + e.getGroupId() + ":" + e.getPluginId(),
                        GroupUserPluginPermissionEntity::getPermissionLevel
                )
        );
    }
}
