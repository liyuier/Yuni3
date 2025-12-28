package com.yuier.yuni.permission.manage;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.core.model.bot.BotModel;
import com.yuier.yuni.core.util.BusinessMappingUtil;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.permission.init.UserPermissionInitProcessor;
import com.yuier.yuni.permission.service.GroupUserPluginPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: UserPermissionManager
 * @Author yuier
 * @Package com.yuier.yuni.permission.manage
 * @Date 2025/12/24 15:57
 * @description: 权限管理器
 */

@Component
public class UserPermissionManager {

    @Autowired
    private GroupUserPluginPermissionService groupUserPluginPermissionService;

    @Autowired
    private UserPermissionInitProcessor userPermissionInitProcessor;

    // 键的格式为 userId@groupId:pluginId
    Map<String, UserPermission> userPermissionMap = new HashMap<>();

    public void initUserPermission() {
        userPermissionMap = userPermissionInitProcessor.loadUserPermissions();
    }

    /**
     * 获取用户权限，查找是否有特殊情况，如果没有，就返回默认权限
     * @param event 群组消息事件
     * @param pluginId 插件 ID
     * @return 用户权限
     */
    public UserPermission getUserPermission(YuniMessageEvent event, String pluginId) {
        UserPermission userPermission = getUserPermissionException(event.getGroupId(), pluginId, event.getUserId());
        if (userPermission != null) {
            return userPermission;
        }
        return getUserDefaultPermission(event);
    }

    /**
     * 获取用户默认权限
     * 如果是群组消息，那么根据 sender 的 role 映射到用户权限
     * 如果是私聊消息，那么返回 USER
     * @param event 消息事件
     * @return 用户权限
     */
    public UserPermission getUserDefaultPermission(YuniMessageEvent event) {
        if (event.isPrivate()) {
            // 判断消息发送者是否为 master
            BotModel botModel = SpringContextUtil.getBean(BotModel.class);
            if (botModel.getMasterId().equals(event.getUserId())) {
                return UserPermission.MASTER;
            }
            return UserPermission.USER;
        }
        // 根据 sender 的 role 映射到用户权限
        return BusinessMappingUtil.roleToPermission(event.getSender().getRole());
    }

    /**
     * 获取用户权限
     * @param groupId  群组 ID
     * @param pluginId  插件 ID
     * @param userId  用户 ID
     * @return 用户权限
     */
    public UserPermission getUserPermissionException(Long groupId, String pluginId, Long userId) {
        // 先在缓存里查找，如果缓存里没有，就返回 null
        String userPermissionKey = assembleUserPermissionKey(groupId, pluginId, userId);
        if (userPermissionMap.containsKey(userPermissionKey)) {
            return userPermissionMap.get(userPermissionKey);
        }
        // 缓存里没有，再去数据库里查
        UserPermission userPermission = groupUserPluginPermissionService.getUserPermission(userId, groupId, pluginId);
        if (userPermission != null) {
            userPermissionMap.put(userPermissionKey, userPermission);
            return userPermission;
        }
        // 数据库里也没有，返回 null
        return null;
    }

    public String assembleUserPermissionKey(Long groupId, String pluginId, Long userId) {
        return userId + "@" + groupId + ":" + pluginId;
    }
}
