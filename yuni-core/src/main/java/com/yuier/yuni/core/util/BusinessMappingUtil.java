package com.yuier.yuni.core.util;

import com.yuier.yuni.core.enums.UserPermission;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.yuier.yuni.core.constants.MessageSenderRole.*;

/**
 * @Title: BusinessMappingUtil
 * @Author yuier
 * @Package com.yuier.yuni.core.util
 * @Date 2025/12/24 16:26
 * @description: 业务概念映射
 */

@Component
public class BusinessMappingUtil {

    static Map<String, UserPermission> roleToPermissionMap = new HashMap<>();

    public BusinessMappingUtil() {
        roleToPermissionMap.put(OWNER, UserPermission.MASTER);
        roleToPermissionMap.put(ADMIN, UserPermission.ADMIN);
        roleToPermissionMap.put(MEMBER, UserPermission.USER);
    }

    public static UserPermission roleToPermission(String role) {
        if (!roleToPermissionMap.containsKey(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        return roleToPermissionMap.get(role);
    }
}
