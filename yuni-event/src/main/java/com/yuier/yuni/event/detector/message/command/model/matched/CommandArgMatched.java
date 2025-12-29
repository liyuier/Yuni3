package com.yuier.yuni.event.detector.message.command.model.matched;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: CommandArgMatched
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command.model.matched
 * @Date 2025/12/23 1:06
 * @description: 匹配出的命令参数
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandArgMatched {

    // 参数名称
    private String name;
    // 参数描述
    private String description;
    // 参数需求的消息段类型，默认为文本
    private CommandArgRequireType requiredType = CommandArgRequireType.PLAIN;

    // 匹配出来的值
    private MessageSegment value;
}
