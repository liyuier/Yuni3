package com.yuier.yuni.event.detector.message.command.model;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: CommandArg
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 0:30
 * @description: 命令参数
 */

@Data
@NoArgsConstructor
public class CommandArg {

    // 参数名称
    private String name = "";
    // 参数描述
    private String description = "";
    // 参数需求的消息段类型，默认为文本
    private CommandArgRequireType requiredType = CommandArgRequireType.PLAIN;

    public Boolean wantsSegmentType(CommandArgRequireType type) {
        return requiredType.equals(type);
    }

}
