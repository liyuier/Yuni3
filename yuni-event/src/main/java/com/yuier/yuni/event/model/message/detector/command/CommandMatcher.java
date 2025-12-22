package com.yuier.yuni.event.model.message.detector.command;

import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.AtSegment;
import com.yuier.yuni.core.model.message.segment.ReplySegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModel;
import com.yuier.yuni.event.model.message.detector.command.model.MessageChainForCommand;
import com.yuier.yuni.event.model.message.detector.command.model.matched.CommandMatched;
import lombok.NoArgsConstructor;

import static com.yuier.yuni.core.constants.MessageSegmentTypes.AT;
import static com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT;
import static com.yuier.yuni.core.constants.SystemConstants.BLANK_SPACE;
import static com.yuier.yuni.core.constants.SystemConstants.FIRST_INDEX;

/**
 * @Title: CommandMatcher
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 1:17
 * @description: 命令匹配器
 */

@NoArgsConstructor
public class CommandMatcher {

    public static MessageChainForCommand chainForCommand = null;

    public static CommandMatched match(CommandModel model, MessageChain chain) {
        CommandMatched matched = new CommandMatched();
        if (model == null | chain == null) {
            return matched;
        }
        // 开始匹配，检查一些基本信息
        // 如果消息链不是以有效文本开头，直接判不匹配
        MessageChainForCommand chainForCommand = convertToMessageChainForCommand(chain);
        return matched ;
    }

    private static MessageChainForCommand convertToMessageChainForCommand(MessageChain chain) {
        // 如果缓存中已经存在，则直接返回
        if (chainForCommand != null) {
            return chainForCommand;
        }
        chainForCommand = new MessageChainForCommand();
        // 遍历消息段，如果消息段为文本消息段，则将文本消息段按空格拆分，并添加到缓存中
        for (MessageSegment segment : chain.getContent()) {
            if (segment.typeOf(TEXT)) {
                // 将文本消息以空格为分割符，拆分为多段
                String[] strArr = ((TextSegment) segment).getText().split(BLANK_SPACE);
                for (String str : strArr) {
                    if (!str.trim().isEmpty()) {
                        chainForCommand.addTextSegment(str);
                    }
                }
            } else {
                chainForCommand.addSegment(segment);
            }
        }
        /* 如果存在回复消息，进行额外处理
          1. 将头部的回复类消息拆下，保存在 chainForCommand 中的一个单独的变量中
          2. 删除一个回复消息自带的 @ 消息
         */
        if (chainForCommand.startWithReplyData()) {
            // 将回复消息段拆下，存入 chainForCommand 的 replyData 字段中
            chainForCommand.setReplyData(((ReplySegment) chainForCommand.removeSegment(FIRST_INDEX)).getData());
            // 获取回复的目标消息的发送者的 ID, 以供后续删除对应的 @ 消息
            // TODO 补充获取消息 ID 的方法
//            Long userId = BotAction.getReplayTargetSenderId(Long.valueOf(chainForCommand.getReplyData().getId()));
            Long userId = 3592194751L;
            // 遍历后续消息段
            for (MessageSegment messageSeg : chainForCommand.getContent()) {
                // 如果发现了与回复消息相匹配的 @ 消息
                if (messageSeg.typeOf(AT) &&
                        ((AtSegment) messageSeg).getQq().equals(String.valueOf(userId))) {
                    // 将此 @ 消息删除
                    chainForCommand.getContent().remove(messageSeg);
                    break;
                }
            }
        }
        return chainForCommand;
    }
}
