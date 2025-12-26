package com.yuier.yuni.event.message.detector.command;

import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.event.context.YuniMessageEvent;
import com.yuier.yuni.event.message.detector.MessageDetector;
import com.yuier.yuni.event.message.detector.command.model.CommandModel;
import com.yuier.yuni.event.message.detector.command.model.matched.CommandMatched;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.yuier.yuni.core.constants.OneBotMessageType.GROUP_MESSAGE;
import static com.yuier.yuni.core.constants.OneBotMessageType.PRIVATE_MESSAGE;

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
        if (!messageTypeMatches(event)) {
            return false;
        }
        CommandMatched commandMatched = CommandMatcher.match(commandModel, event.getMessageChain());
        if (commandMatched.getMatchSuccess()) {
            event.setCommandMatched(commandMatched);
            return true;
        }
        return false;
    }

    @Override
    public MessageType listenAt() {
        return MessageType.GROUP;
    }

    public Boolean messageTypeMatches(YuniMessageEvent event) {
        return (listenAt() == MessageType.GROUP && event.getMessageType().equals(GROUP_MESSAGE)) ||
                (listenAt() == MessageType.PRIVATE && event.getMessageType().equals(PRIVATE_MESSAGE));
    }
}
