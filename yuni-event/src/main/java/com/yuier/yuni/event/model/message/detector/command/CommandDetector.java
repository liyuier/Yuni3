package com.yuier.yuni.event.model.message.detector.command;

import com.yuier.yuni.event.model.context.YuniMessageEvent;
import com.yuier.yuni.event.model.message.detector.MessageDetector;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModel;
import com.yuier.yuni.event.model.message.detector.command.model.matched.CommandMatched;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Title: CommandDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 1:19
 * @description: 命令探测器
 */

@Data
@AllArgsConstructor
public class CommandDetector implements MessageDetector {

    private CommandModel commandModel;

    @Override
    public Boolean match(YuniMessageEvent event) {
        CommandMatched commandMatched = CommandMatcher.match(commandModel, event.getMessageChain());
        if (commandMatched.getMatchSuccess()) {
            event.setCommandMatched(commandMatched);
            return true;
        }
        return false;
    }
}
