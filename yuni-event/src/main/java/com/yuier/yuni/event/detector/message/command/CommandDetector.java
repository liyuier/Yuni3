package com.yuier.yuni.event.detector.message.command;

import com.yuier.yuni.core.bot.BotMessageInfo;
import com.yuier.yuni.core.bot.YuniBot;
import com.yuier.yuni.core.constants.MessageSegmentTypes;
import com.yuier.yuni.core.enums.MessageType;
import com.yuier.yuni.core.event.YuniMessageEvent;
import com.yuier.yuni.core.event.matched.CommandResult;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.AtSegment;
import com.yuier.yuni.core.model.message.segment.ReplySegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.core.model.bot.BotApp;
import com.yuier.yuni.event.detector.message.MessageDetector;
import com.yuier.yuni.event.detector.message.command.model.CommandNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.yuier.yuni.core.constants.SystemConstants.BLANK_SPACE;
import static com.yuier.yuni.core.constants.SystemConstants.FIRST_INDEX;
import static com.yuier.yuni.core.constants.YuniMessageType.GROUP_MESSAGE;
import static com.yuier.yuni.core.constants.YuniMessageType.PRIVATE_MESSAGE;

/**
 * 命令节点探测器 —— 使用 CommandNode + CommandMatcher 替代旧的 CommandModel + CommandMatcher。
 *
 * <p>插件在 {@code getDetector()} 中返回本探测器即可迁移到新命令系统。</p>
 */
@Data
@AllArgsConstructor
public class CommandDetector implements MessageDetector {

    private CommandNode rootNode;

    @Override
    public Boolean match(YuniMessageEvent event) {
        if (!messageTypeMatches(event)) {
            return false;
        }

        // tokenize → CommandTokens（含抽离的回复消息段）
        CommandTokens tokens = tokenize(event.getMessageChain());
        if (tokens.isEmpty()) {
            return false;
        }

        // 检查命令头是否以 commandFlag 开头
        String commandFlag = getBotApp().getCommandFlag();
        if (!(tokens.get(FIRST_INDEX) instanceof TextSegment)) {
            return false;
        }
        String first = ((TextSegment) tokens.get(FIRST_INDEX)).getText();
        if (!first.startsWith(commandFlag)) {
            return false;
        }

        // 去掉命令前缀（将 "/echo" 变为 "echo"）
        String stripped = first.substring(commandFlag.length());
        if (stripped.isEmpty()) {
            return false;
        }
        List<MessageSegment> mutableTokens = new ArrayList<>(tokens.getTokens());
        mutableTokens.set(FIRST_INDEX, new TextSegment(stripped));
        CommandTokens commandTokens = new CommandTokens(mutableTokens, tokens.getReply());

        // 匹配
        CommandMatcher matcher = new CommandMatcher();
        CommandResult result = matcher.match(commandTokens, rootNode);

        if (result.isMatchSuccess()) {
            event.setCommandResult(result);
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

    /**
     * 将消息链 tokenize。
     *
     * <p>规则：</p>
     * <ul>
     *   <li>文本按空格拆分为多个 TextSegment</li>
     *   <li>非文本消息段原样保留</li>
     *   <li>首部回复消息段被抽离到 {@link CommandTokens#getReply()}，其对应的 @ 从 token 流中移除</li>
     * </ul>
     */
    static CommandTokens tokenize(MessageChain chain) {
        List<MessageSegment> rawTokens = new ArrayList<>();

        // 初步处理，先按消息段对消息链进行拆分；其中，对于文本消息段，进一步按空格进行拆分。结果输出为 rawToken
        for (MessageSegment segment : chain.getContent()) {
            if (segment.typeOf(MessageSegmentTypes.TEXT)) {
                String[] parts = ((TextSegment) segment).getText().split(BLANK_SPACE);
                for (String part : parts) {
                    if (!part.isBlank()) {
                        rawTokens.add(new TextSegment(part.strip()));
                    }
                }
            } else {
                rawTokens.add(segment);
            }
        }

        /*
        QQ 消息链具有以下特性：
        1. 包含回复消息的消息链中，无论用户实际上编辑消息时，在何时对消息插入回复，发送到后端的消息链中，回复消息段必定会被放在完整消息链的第一个消息段处
        2. 一条完整消息链中至多可能存在一个回复消息段。
        为此，将首部回复消息单独提取保存；匹配流程中，当试图匹配回复消息段时，取此处单独保存下来的回复消息段进行匹配
         */
        ReplySegment reply = null;
        if (!rawTokens.isEmpty() && rawTokens.get(FIRST_INDEX).typeOf(MessageSegmentTypes.REPLY)) {
            reply = (ReplySegment) rawTokens.remove(FIRST_INDEX);
            BotMessageInfo msg = getYuniBot().getMessage(reply.getId()).orElse(null);
            Long userId = msg != null ? msg.getUserId() : 0L;
            rawTokens.removeIf(seg ->
                    seg.typeOf(MessageSegmentTypes.AT) &&
                            ((AtSegment) seg).getQq().equals(String.valueOf(userId)));
        }

        return new CommandTokens(rawTokens, reply);
    }

    private static YuniBot getYuniBot() {
        return SpringContextUtil.getBean(YuniBot.class);
    }

    private static BotApp getBotApp() {
        return SpringContextUtil.getBean(BotApp.class);
    }
}
