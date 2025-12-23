package com.yuier.yuni.event.model.message.detector.command.model;

import com.yuier.yuni.core.enums.UserPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: CommandOption
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 0:31
 * @description: 命令选项
 */

@Data
@NoArgsConstructor
public class CommandOption {

    // 选项标识符
    private String flag;

    // 选项参数，一个选项只允许接收一个参数
    private CommandArg requiredArg = null;

    private CommandArg optionalArg = null;

    // 该选项需求的权限，默认为普通用户
    private UserPermission permission = UserPermission.USER;
}
