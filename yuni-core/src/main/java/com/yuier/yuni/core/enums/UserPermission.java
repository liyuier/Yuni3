package com.yuier.yuni.core.enums;

import lombok.Data;

/**
 * @Title: UserPermission
 * @Author yuier
 * @Package com.yuier.yuni.core.enums
 * @Date 2025/12/23 0:41
 * @description: 用户权限分级
 */

public enum UserPermission {

    // 机器人拥有者
    MASTER(30),

    // 管理员
    ADMIN(20),

    // 普通用户
    USER(10),

    // 封禁用户
    BLOCKED(0);

    private int priority;

    UserPermission(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
