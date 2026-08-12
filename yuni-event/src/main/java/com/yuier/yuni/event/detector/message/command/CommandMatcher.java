package com.yuier.yuni.event.detector.message.command;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.event.matched.ArgResult;
import com.yuier.yuni.core.event.matched.CommandResult;
import com.yuier.yuni.core.constants.MessageSegmentTypes;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.event.detector.message.command.model.ArgDef;
import com.yuier.yuni.event.detector.message.command.model.CommandNode;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 命令节点匹配器 —— 对命令树执行带回溯的递归匹配。
 *
 * <p>输入为 {@link CommandTokens}（已 tokenize 的列表 + 抽离的回复消息段）。
 * 匹配 {@code REPLY} 类型参数时不从当前位置消耗 token，而是从
 * {@link CommandTokens#getReply()} 读取。</p>
 *
 * <p>匹配顺序：位置参数 → 选项（便宜） → 子命令（回溯）</p>
 */
@Slf4j
public class CommandMatcher {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    public CommandResult match(CommandTokens tokens, CommandNode rootNode) {
        if (tokens == null || tokens.isEmpty() || rootNode == null) {
            return CommandResult.fail();
        }
        CommandResult result = matchInternal(tokens, 0, rootNode);
        // 根层级不得有未消费的 token
        if (result.isMatchSuccess() && result.getConsumedCount() < tokens.size()) {
            return CommandResult.fail();
        }
        return result;
    }

    private CommandResult matchInternal(CommandTokens tokens, int start, CommandNode node) {
        // 0. 匹配本节点标识
        if (start >= tokens.size()) {
            return CommandResult.fail();
        }
        MessageSegment first = tokens.get(start);
        if (!first.typeOf(MessageSegmentTypes.TEXT)
                || !((TextSegment) first).getText().equals(node.getName())) {
            return CommandResult.fail();
        }

        // 本节点标识匹配通过，进入下一 token
        int pos = start + 1;

        CommandResult result = new CommandResult();
        result.setMatchedKey(node.getName());
        result.setMatchSuccess(true);

        // 1. 匹配位置参数
        for (ArgDef arg : node.getArgs()) {
            if (arg.getType() == CommandArgRequireType.REPLY) {
                // REPLY 类型：不从 token 流取，而是从 tokens.getReply() 读取
                if (arg.isRequired() && !tokens.hasReply()) {
                    return CommandResult.fail();
                }
                if (tokens.hasReply()) {
                    result.getArgs().put(arg.getName(),
                            new ArgResult(arg.getName(), CommandArgRequireType.REPLY, tokens.getReply()));
                }
                // 不推进 pos —— 回复消息段不在 token 流中
                continue;
            }

            if (pos >= tokens.size()) {
                // token 已耗尽。Builder 保证必选→可选→变长的顺序，
                // 当前及后续参数均为非必选，直接退出循环
                if (arg.isRequired()) return CommandResult.fail();
                break;
            }

            // 当匹配到变长参数时，隐含一个前提：当前命令节点下的参数匹配来到了最后
            if (arg.isVariadic()) {
                // 非贪婪：遇到当前节点的选项或子命令名时停止。
                // 子命令的选项/子命令必须先匹配到子命令本身才能生效，此处无需递归检查
                List<MessageSegment> consumed = new ArrayList<>();
                for (int i = pos; i < tokens.size(); i++) {
                    MessageSegment seg = tokens.get(i);
                    if (seg.typeOf(MessageSegmentTypes.TEXT)) {
                        String text = ((TextSegment) seg).getText();
                        if (node.getOptions().contains(text) || isChildName(node, text)) {
                            break;
                        }
                    }
                    if (!typeMatches(seg, arg.getType())) return CommandResult.fail();
                    consumed.add(seg);
                }
                // 必选变长：至少消费一个 token
                if (arg.isRequired() && consumed.isEmpty()) return CommandResult.fail();
                result.getArgs().put(arg.getName(),
                        new ArgResult(arg.getName(), arg.getType(), consumed));
                pos += consumed.size();
            } else {
                // 单值参数
                if (typeMatches(tokens.get(pos), arg.getType())) {
                    result.getArgs().put(arg.getName(),
                            new ArgResult(arg.getName(), arg.getType(), tokens.get(pos)));
                    pos++;
                } else if (arg.isRequired()) {
                    return CommandResult.fail();
                }
                // 可选参数类型不匹配 → 跳过，不占用 token
            }
        }

        // 2. 匹配选项 + 子命令
        if (!node.getOptions().isEmpty() || !node.getChildren().isEmpty()) {
            while (pos < tokens.size()) {
                MessageSegment seg = tokens.get(pos);

                // 2a. 选项
                if (seg.typeOf(MessageSegmentTypes.TEXT)) {
                    String text = ((TextSegment) seg).getText();
                    if (node.getOptions().contains(text)) {
                        result.getOptions().add(text);
                        pos++;
                        continue;
                    }
                }

                // 2b. 子命令（回溯）
                boolean matched = false;
                for (CommandNode child : node.getChildren()) {
                    CommandResult childResult = matchInternal(tokens, pos, child);
                    if (childResult.isMatchSuccess()) {
                        // 匹配到一条子命令，就跳出 while 循环，在循环外重置 pos 等值
                        result.getChildren().put(child.getName(), childResult);
                        pos += childResult.getConsumedCount();
                        matched = true;
                        break;
                    }
                }
                // 如果所有子命令均匹配完，也没匹配到，为避免不消费 token 导致的无限循环，也跳出 while 循环
                if (!matched) {
                    break;
                }
            }
        }

        result.setConsumedCount(pos - start);

        // 3. 校验必须子命令
        for (CommandNode child : node.getChildren()) {
            if (child.isRequired() && !result.getChildren().containsKey(child.getName())) {
                return CommandResult.fail();
            }
        }

        // 4. 校验 requiresChild
        if (node.isRequiresChild() && result.getOptions().isEmpty() && result.getChildren().isEmpty()) {
            return CommandResult.fail();
        }

        return result;
    }

    static boolean typeMatches(MessageSegment seg, CommandArgRequireType type) {
        switch (type) {
            case PLAIN:  return seg.typeOf(MessageSegmentTypes.TEXT);
            case NUMBER:
                if (!seg.typeOf(MessageSegmentTypes.TEXT)) return false;
                return NUMBER_PATTERN.matcher(((TextSegment) seg).getText()).matches();
            case AT:     return seg.typeOf(MessageSegmentTypes.AT);
            case IMAGE:  return seg.typeOf(MessageSegmentTypes.IMAGE);
            case URL:
                if (!seg.typeOf(MessageSegmentTypes.TEXT)) return false;
                return isValidUrl(((TextSegment) seg).getText());
            case REPLY:  return seg.typeOf(MessageSegmentTypes.REPLY);
            default:     return false;
        }
    }

    private static boolean isValidUrl(String url) {
        try { new URL(url); return true;
        } catch (MalformedURLException e) { return false; }
    }

    private static boolean isChildName(CommandNode node, String text) {
        for (CommandNode child : node.getChildren()) {
            if (child.getName().equals(text)) return true;
        }
        return false;
    }
}
