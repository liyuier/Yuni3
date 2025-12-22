package com.yuier.yuni.event.model.message.detector.command.model.matched;

import com.yuier.yuni.core.enums.UserPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Title: CommandMatched
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 1:03
 * @description: 匹配出的结果
 */

@Data
@NoArgsConstructor
public class CommandMatched {

    // 命令头
    private String head;

    // 命令权限。对于注册了命令的插件，默认以命令权限为插件权限。
    // 默认为 USER
    private UserPermission permission = UserPermission.USER;

    // 以参数的 name 为 key，匹配出的参数为 value
    private Map<String, CommandArgMatched> argsMatchedout;

    // 以选项的 flag 为 key，匹配出的选项为 value
    private Map<String, CommandOptionMatched> optionsMatchedout;

    // 以子命令的 head 为 key，匹配出的子命令为 value
    private Map<String, CommandMatched> childCommandsMatchedout;

    // 是否匹配成功
    private Boolean matchSuccess = false;
}
