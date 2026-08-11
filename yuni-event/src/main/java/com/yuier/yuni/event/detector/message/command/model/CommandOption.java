package com.yuier.yuni.event.detector.message.command.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated 请使用 {@link CommandNode#asOption} 创建选项节点。新 API 支持多参数选项和选项必须约束。
 *
 * @Title: CommandOption
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 0:31
 * @description: 命令选项
 */
@Deprecated

@Data
@NoArgsConstructor
public class CommandOption {

    // 选项标识符
    private String flag;

    // 选项参数，一个选项只允许接收一个参数
    private CommandArg requiredArg = null;

    private CommandArg optionalArg = null;

}
