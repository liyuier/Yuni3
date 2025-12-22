package com.yuier.yuni.event.model.message.detector.command.model;

import com.yuier.yuni.core.enums.UserPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: CommandModel
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 0:29
 * @description: 命令建模
 */

@Data
@NoArgsConstructor
public class CommandModel {

    // 命令头
    private String head;

    // 命令参数，分为必选参数与可选参数
    private List<CommandArg> requiredArgs = new ArrayList<>();
    private List<CommandArg> optionalArgs = new ArrayList<>();

    // 命令选项
    private List<CommandOption> options = new ArrayList<>();

    // 子命令列表
    private List<CommandModel> childCommands = new ArrayList<>();

    // 命令权限。对于注册了命令的插件，默认以命令权限为插件权限。
    // 默认为 USER
    private UserPermission permission = UserPermission.USER;
}
