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

    // 命令权限。对于注册了命令的插件，默认以命令权限为插件权限。
    // 默认为 USER
    private UserPermission permission = UserPermission.USER;

    /**
     * @return  命令需要的最少消息段数
     */
    public Integer requiredLeastSegNum() {
        return requiredArgs.size() + 1;
    }

    /**
     * 命令是否有选项
     * @return  有选项：true
     */
    public Boolean hasOptions() {
        return !options.isEmpty();
    }

    public Boolean hasRequiredArgs() {
        return !requiredArgs.isEmpty();
    }

    public Boolean hasOptionalArgs() {
        return !optionalArgs.isEmpty();
    }

    public Boolean hasArgs() {
        return hasRequiredArgs() || hasOptionalArgs();
    }

    public Boolean hasArg(String name) {
        for (CommandArg arg : requiredArgs) {
            if (arg.getName().equals(name)) {
                return true;
            }
        }
        for (CommandArg arg : optionalArgs) {
            if (arg.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public CommandArg getArg(String name) {
        for (CommandArg arg : requiredArgs) {
            if (arg.getName().equals(name)) {
                return arg;
            }
        }
        for (CommandArg arg : optionalArgs) {
            if (arg.getName().equals(name)) {
                return arg;
            }
        }
        return null;
    }

    public Boolean hasOption(String flag) {
        for (CommandOption option : options) {
            if (option.getFlag().equals(flag)) {
                return true;
            }
        }
        return false;
    }

    public CommandOption getOption(String flag) {
        for (CommandOption option : options) {
            if (option.getFlag().equals(flag)) {
                return option;
            }
        }
        return null;
    }
}
