package com.yuier.yuni.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.permission.mapper.GroupUserPluginPermissionMapper;
import com.yuier.yuni.permission.service.GroupUserPluginPermissionService;
import com.yuier.yuni.permission.domain.entity.GroupUserPluginPermissionEntity;
import org.springframework.stereotype.Service;

/**
 * (GroupUserPluginPermission)表服务实现类
 *
 * @author liyuier
 * @since 2025-12-24 15:48:35
 */
@Service
public class GroupUserPluginPermissionServiceImpl extends ServiceImpl<GroupUserPluginPermissionMapper, GroupUserPluginPermissionEntity> implements GroupUserPluginPermissionService {

    // 根据用户 ID 、群组 ID 与插件 ID 查找权限等级
    @Override
    public UserPermission getUserPermission(Long userId, Long groupId, String pluginId) {
        // 使用 wrapper 查询
        LambdaQueryWrapper<GroupUserPluginPermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupUserPluginPermissionEntity::getUserId, userId)
                .eq(GroupUserPluginPermissionEntity::getGroupId, groupId)
                .eq(GroupUserPluginPermissionEntity::getPluginId, pluginId);
        GroupUserPluginPermissionEntity entity = getOne(queryWrapper);
        return entity == null ? null : entity.getPermissionLevel();
    }

}

