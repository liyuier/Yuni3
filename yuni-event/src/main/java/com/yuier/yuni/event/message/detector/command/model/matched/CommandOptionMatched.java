package com.yuier.yuni.event.message.detector.command.model.matched;

import com.yuier.yuni.core.enums.UserPermission;
import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: CommandOptionMatched
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command.model.matched
 * @Date 2025/12/23 1:07
 * @description: 匹配出的命令选项
 */

@Data
@NoArgsConstructor
public class CommandOptionMatched {

    // 选项标识符
    private String flag;

    // 选项参数，一个选项只允许接收一个参数
    private CommandArgMatched requiredArg = null;
    private CommandArgMatched optionalArg = null;

    // 该选项需求的权限，默认为普通用户
    private UserPermission permission = UserPermission.USER;

    public MessageSegment getRequiredArgValue() {
        return requiredArg == null ? null : requiredArg.getValue();
    }

    public MessageSegment getOptionalArgValue() {
        return optionalArg == null ? null : optionalArg.getValue();
    }


    // 传入 flag 的构造函数
    public CommandOptionMatched(String flag) {
        this.flag = flag;
    }

    // 判断是否存在必选参数
    public Boolean hasRequiredArg() {
        return requiredArg != null;
    }

    // 判断是否存在可选参数
    public Boolean hasOptionalArg() {
        return optionalArg != null;
    }

    // 判断是否存在参数
    public Boolean hasArg() {
        return hasRequiredArg() || hasOptionalArg();
    }
}
