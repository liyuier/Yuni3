package com.yuier.yuni.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.permission.domain.entity.GroupUserPluginPermissionEntity;

/**
 * (GroupUserPluginPermission)表服务接口
 *
 * @author liyuier
 * @since 2025-12-24 15:48:34
 */
public interface GroupUserPluginPermissionService extends IService<GroupUserPluginPermissionEntity> {

    UserPermission getUserPermission(Long userId, Long groupId, String pluginId);

}

