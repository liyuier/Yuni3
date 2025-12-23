package com.yuier.yuni.event.model.message.detector.command;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.core.constants.MessageSegmentTypes;
import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.model.event.MessageEvent;
import com.yuier.yuni.core.model.message.MessageChain;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.AtSegment;
import com.yuier.yuni.core.model.message.segment.ReplySegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import com.yuier.yuni.core.util.SpringContextUtil;
import com.yuier.yuni.event.model.config.BotApp;
import com.yuier.yuni.event.model.message.detector.command.model.CommandArg;
import com.yuier.yuni.event.model.message.detector.command.model.CommandModel;
import com.yuier.yuni.event.model.message.detector.command.model.CommandOption;
import com.yuier.yuni.event.model.message.detector.command.model.MessageChainForCommand;
import com.yuier.yuni.event.model.message.detector.command.model.matched.CommandArgMatched;
import com.yuier.yuni.event.model.message.detector.command.model.matched.CommandMatched;
import com.yuier.yuni.event.model.message.detector.command.model.matched.CommandOptionMatched;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        CommandMatched commandMatched = new CommandMatched();
        if (model == null || chain == null) {
            return notMatch();
        }
        // 开始匹配，检查一些基本信息
        // 如果消息链不是以有效文本开头，直接判不匹配
        MessageChainForCommand chainForCommand = convertToMessageChainForCommand(chain);
        if (!chainForCommand.startWithTextData()) {
            return notMatch();
        }
        /*
        检查消息链开头的文本消息段能否匹配指令
        这里不检查消息头，消息头应当是注册的插件的指令头，这里不做检查
         */
        String commandFlag = getBotApp().getCommandFlag();
        TextSegment textSegment = (TextSegment) chainForCommand.getContent().get(FIRST_INDEX);
        if (!textSegment.getText().equals(commandFlag + model.getHead())) {
            return notMatch();
        }
        // 如果拆分出来的消息段数量不能满足指令的最低个数，则判不匹配
        if (chainForCommand.restMessageSegNum() < model.requiredLeastSegNum()) {
            return notMatch();
        }
        /* 基本信息检查结束，下面开始正式匹配 */

        /*
         * 匹配参数与选项
         * 思路：
         * 1. 先匹配必要参数，所有必要参数都要能匹配上才能进入后续步骤
         * 2. 必要参数匹配结束，尝试匹配可选参数
         * 3. 最后匹配指令选项
         * 匹配过程采用非贪婪匹配，即如果某段文本消息与选项标识匹配，那么就停止匹配可选参数，开始匹配该选项
         */

        // 由于上文已经检查了消息的第一个消息段可以匹配指令头，所以此时从第二个消息段开始匹配
        chainForCommand.setCurSegIndex(FIRST_INDEX + 1);

        // 匹配必选参数，如果匹配不上，直接返回 false
        Map<String, CommandArgMatched> argsMatched = new HashMap<>();
        Map<String, CommandArgMatched> stringCommandArgMatchedMap = matchRequiredArgs(chainForCommand, model);
        if (stringCommandArgMatchedMap ==  null) {
            return notMatch();
        }
        // 如果 cfc 已经遍历完毕，则返回匹配成功
        if (chainForCommand.messageSegsMatchedEnd()) {
            // 将匹配出的必选参数放入 commandMatched 中
            commandMatched.setArgsMatchedout(argsMatched);
            commandMatched.setMatchSuccess(true);
            return commandMatched;
        }

        // 匹配可选参数
        // 可选参数的匹配失败不会直接导致整个逻辑的失败，因此这里定义的方法为 void 类型
        matchOptionalArgs(chainForCommand, model, argsMatched);
        // 如果 cfc 已经遍历完毕，则返回匹配成功
        if (chainForCommand.messageSegsMatchedEnd()) {
            // 将匹配出的必选参数放入 commandMatched 中
            commandMatched.setArgsMatchedout(argsMatched);
            commandMatched.setMatchSuccess(true);
            return commandMatched;
        }

        /*
         * 指令参数匹配完毕，开始匹配选项
         */
        Map<String, CommandOptionMatched> optionsMatched = new HashMap<>();
        matchOptions(chainForCommand, model, optionsMatched);
        if (chainForCommand.messageSegsMatchedEnd()) {
            // 将匹配出的必选参数放入 commandMatched 中
            commandMatched.setArgsMatchedout(argsMatched);
            commandMatched.setOptionsMatchedout(optionsMatched);
            commandMatched.setMatchSuccess(true);
            return commandMatched;
        }

        commandMatched.setMatchSuccess(false);
        return commandMatched ;
    }

    /**
     * 匹配选项
     * @param chainForCommand 命令消息链
     * @param model 命令建模
     * @param optionsMatched 选项匹配结果
     */
    private static void matchOptions(MessageChainForCommand chainForCommand, CommandModel model, Map<String, CommandOptionMatched> optionsMatched) {
        // 逻辑跟匹配可选参数的逻辑一模一样，只是在判断能否匹配的地方不同
        for (CommandOption option : model.getOptions()) {
            if (chainForCommand.messageSegsMatchedEnd()) {
                return;
            }
            MessageSegment curMessageSeg = chainForCommand.getCurMessageSeg();
            // 如果当前消息段不是文本消息，则返回
            if (!curMessageSeg.typeOf(MessageSegmentTypes.TEXT)) {
                return;
            }
            // 如果不能匹配上任意一个选项，则返回
            String text = ((TextSegment) curMessageSeg).getText();
            if (!model.hasOption(text)) {
                return;
            }
            // 匹配成功，构建选项匹配结果
            CommandOptionMatched commandOptionMatched = new CommandOptionMatched(text);
            CommandOption optionModel = model.getOption(text);
            // 如果选项参数不为空，那么需要继续匹配参数
            CommandArg optionArg = optionModel.getArg();
            if (optionArg != null) {
                if (chainForCommand.messageSegsMatchedEnd()) {
                    return;
                }

                // 如果参数需求回复消息，需要特殊处理
                if (optionArg.wantsSegmentType(CommandArgRequireType.REPLY)) {
                    if (chainForCommand.storesReplyData()) {
                        // 获取回复消息
                        MessageSegment replySegment = chainForCommand.getReplySegment();
                        // 构建参数匹配结果
                        commandOptionMatched.setArg(new CommandArgMatched(
                                optionArg.getName(),
                                optionArg.getDescription(),
                                optionArg.getRequiredType(),
                                replySegment
                        ));
                        // 当前选项匹配完毕，继续匹配下一个选项
                        // 保存选项匹配结果
                        optionsMatched.put(text, commandOptionMatched);
                        continue;
                    } else {
                        // 参数不为空，但找不到需求的消息段，参数匹配失败，返回
                        return;
                    }
                }

                // 指针向右移动一格
                chainForCommand.curSegIndexStepForwardBy(1);
                MessageSegment nextMessageSeg = chainForCommand.getCurMessageSeg();
                if (messageSegmentMatcherArg(nextMessageSeg, optionArg)) {
                    // 下一个消息段恰好匹配参数，则将参数值放入选项匹配结果中
                    commandOptionMatched.setArg(new CommandArgMatched(
                            optionArg.getName(),
                            optionArg.getDescription(),
                            optionArg.getRequiredType(),
                            nextMessageSeg
                    ));
                } else {
                    // 参数不为空，但找不到需求的消息段，参数匹配失败，返回
                    return;
                }
            }
            // 保存选项匹配结果
            optionsMatched.put(text, commandOptionMatched);

            // 指针向右移动一格
            chainForCommand.curSegIndexStepForwardBy(1);
        }
    }

    /**
     * 匹配可选参数
     * @param chainForCommand 命令消息链
     * @param model 命令建模
     * @param argsMatched 参数匹配结果
     */
    private static void matchOptionalArgs(MessageChainForCommand chainForCommand, CommandModel model, Map<String, CommandArgMatched> argsMatched) {
        /*
         * 采用 “先到先得，过时不候” 的方式匹配可选参数
         * 具体来说，使用指针单向遍历消息段，并使用 for 循环遍历可选参数。
         * 对于每个消息段，遍历每个可选参数，将消息段匹配给第一个可匹配的参数，随后消息段指针右移，for 循环也进入下一轮，可选参数不回头遍历。
         * 举例来说：
         * 有如下 4 个可选参数：甲-at、乙-number、丙-at、丁-number
         * 有如下消息段：<123>、<@1>、<@2>
         * 也许会有读者认为应该如此匹配：<123>-乙，<@1>-甲，<@2>-丙，但实际并非如此。
         * 1. 对于消息段 <123>，从甲开始遍历可选参数，第一轮搜索匹配上了乙，消息段指针右移；参数从丙开始遍历
         * 2. 对于消息段 <@1>，从丙开始查找接收 @ 消息的可选参数，匹配上了丙
         * 3. 对于消息段 <@2>，从丁开始查找接收 @ 消息的可选参数，找不到
         * 最终结果为：<123>-乙，<@1>-丙，<@2>-未匹配。
         * 这样的匹配原则规则明确，方便理解。
         *
         * 鉴于以上 feature，我推荐这样的指令定义策略：
         * 1. 不将不同类型的可选参数混杂定义，如上方示例，at 类参数与 number 类参数混杂定义，极易出现不符合预期的匹配结果。
         * 2. 不定义连续多个可选参数，例如 3 个以上。超过 3 个可选参数，很难说清自己接收到的数据到底是不是用户想要传达的意图。
         * 3. 多用选项代替可选参数。实际上这样的模式可以理解为一种 “有名字的可选参数”，其匹配逻辑更精准，代码更健壮。
         */

        // 因为 cfc 保存了 curSegIndex，无需手动遍历消息段，因此这里直接遍历可选参数
        for (CommandArg arg : model.getOptionalArgs()) {
            /*
             与必选参数不同，一段消息可能无法匹配任意可选参数，因此这里需要检查消息段指针是否越界
             */
            if (chainForCommand.messageSegsMatchedEnd()) {
                return;
            }
            MessageSegment curMessageSeg = chainForCommand.getCurMessageSeg();

            /*
             非贪婪匹配的实现
             如果匹配可选参数时，匹配到某个选项标识，那么跳出匹配
             */
            if (curMessageSeg.typeOf(MessageSegmentTypes.TEXT)) {
                String text = ((TextSegment) curMessageSeg).getText();
                if (model.hasOption(text)) {
                    return;
                }
            }

            // 如果当前参数需求回复消息段，需要特殊处理
            // 俺寻思把必选参数那块复制过来改改就行
            if (arg.wantsSegmentType(CommandArgRequireType.REPLY)) {
                // 如果 cfc 中没有储存回复消息段，直接进入下一轮循环，匹配下一个可选参数
                if (!chainForCommand.storesReplyData()) {
                    continue;
                }
                // 如果 cfc 中储存了回复消息，该参数直接匹配上
                // 将该参数 加入匹配结果中
                /* WARN 如果在指令中定义了多个回复消息参数，那么这里会出问题
                * 不过显然一条正常的 QQ 消息中不会出现多个回复消息段，正常也不会有人这么定义一条命令 */
                argsMatched.put(arg.getName(), new CommandArgMatched(
                        arg.getName(),
                        arg.getDescription(),
                        CommandArgRequireType.REPLY,
                        chainForCommand.getReplySegment())
                );
                /*
                 上文匹配到的回复消息段并不是从当前位置取出的，而是直接从 cfc 中取出的
                 因此进入下一段循环时 cfc.curSegIndex 不需要移动
                 总消息指针，不动！
                 */
                continue;
            } else {
                if (!messageSegmentMatcherArg(curMessageSeg, arg)) {
                    // 如果当前消息段不能匹配该参数，则返回不匹配
                    continue;
                }
            }
            // 当前参数不期望回复消息，并且可以与当前消息段匹配上
            // 保存该参数
            argsMatched.put(arg.getName(), new CommandArgMatched(
                    arg.getName(),
                    arg.getDescription(),
                    arg.getRequiredType(),
                    curMessageSeg)
            );
            // 将指针向右移动一位
            chainForCommand.curSegIndexStepForwardBy(1);
        }
    }

    /**
     * 匹配必选参数
     * @param chainForCommand 命令消息链
     * @param model 命令建模
     * @return 匹配结果
     */
    private static Map<String, CommandArgMatched> matchRequiredArgs(MessageChainForCommand chainForCommand, CommandModel model) {
        Map<String, CommandArgMatched> argsMatched = new HashMap<>();
        // 遍历所有必选参数，检查是否可以与消息段按顺序匹配上
        // 因为在 match 函数前半部分检查过参数消息量必然能满足最低要求，因此这里不需要考虑消息段指针越界问题
        List<CommandArg> requiredArgs = model.getRequiredArgs();
        for (CommandArg arg : requiredArgs) {
            // 取出当前消息段
            MessageSegment curMessageSeg = chainForCommand.getCurMessageSeg();
            // 如果当前参数期望回复消息段，需要特殊处理
            if (arg.wantsSegmentType(CommandArgRequireType.REPLY)) {
                // 如果 cfc 中没有储存回复消息段，直接返回不匹配
                if (!chainForCommand.storesReplyData()) {
                    return null;
                }
                // 如果 cfc 中储存了回复消息，该参数直接匹配上
                // 将该参数 加入匹配结果中
                argsMatched.put(arg.getName(), new CommandArgMatched(
                        arg.getName(),
                        arg.getDescription(),
                        CommandArgRequireType.REPLY,
                        chainForCommand.getReplySegment())
                );
                /*
                 上文匹配到的回复消息段并不是从当前位置取出的，而是直接从 cfc 中取出的
                 因此进入下一段循环时 cfc.curSegIndex 不需要移动
                 */
                continue;
            } else {
                if (!messageSegmentMatcherArg(curMessageSeg, arg)) {
                    // 如果当前消息段不能匹配该参数，则返回不匹配
                    return null;
                }
            }
            // 如果当前消息段能匹配该参数，则将该参数加入匹配结果中
            argsMatched.put(arg.getName(), new CommandArgMatched(
                    arg.getName(),
                    arg.getDescription(),
                    arg.getRequiredType(),
                    curMessageSeg)
            );
            // 将指针向右移动一位
            chainForCommand.curSegIndexStepForwardBy(1);
        }
        // 所有必要参数均成功匹配结果，返回
        return argsMatched;
    }

    /**
     * 判断当前消息段是否能匹配参数
     * @param messageSeg 当前消息段
     * @param arg 参数
     * @return 匹配结果
     */
    private static Boolean messageSegmentMatcherArg(MessageSegment messageSeg, CommandArg arg) {
        final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
        switch (arg.getRequiredType()) {
            case PLAIN -> {
                return messageSeg.typeOf(MessageSegmentTypes.TEXT);
            }
            case NUMBER -> {
                if (!messageSeg.typeOf(MessageSegmentTypes.TEXT)) {
                    return false;
                }
                String segText = ((TextSegment) messageSeg).getText();
                return NUMBER_PATTERN.matcher(segText).matches();
            }
            case AT -> {
                return messageSeg.typeOf(MessageSegmentTypes.AT);
            }
            case IMAGE -> {
                return messageSeg.typeOf(MessageSegmentTypes.IMAGE);
            }
            case URL -> {
                if (!messageSeg.typeOf(MessageSegmentTypes.TEXT)) {
                    return false;
                }
                String segText = ((TextSegment) messageSeg).getData().getText();
                return isValidUrl(segText);
            }
            case REPLY -> {
                return messageSeg.typeOf(MessageSegmentTypes.REPLY);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * 检查一段字符串是否为 URL
     * @param url  待检查的字符串
     * @return  是否为合法 URL
     */
    private static Boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 将消息链转换为命令消息链
     * @param chain 消息链
     * @return 命令消息链
     */
    private static MessageChainForCommand convertToMessageChainForCommand(MessageChain chain) {
        // 如果缓存中已经存在，则直接返回
        if (chainForCommand != null) {
            chainForCommand.resetCurSegIndex();
            return chainForCommand;
        }
        chainForCommand = new MessageChainForCommand();
        // 遍历消息段，如果消息段为文本消息段，则将文本消息段按空格拆分，并添加到缓存中
        for (MessageSegment segment : chain.getContent()) {
            if (segment.typeOf(MessageSegmentTypes.TEXT)) {
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
            chainForCommand.setReplySegment((ReplySegment) chainForCommand.removeSegment(FIRST_INDEX));
            // 获取回复的目标消息的发送者的 ID, 以供后续删除对应的 @ 消息
            OneBotAdapter oneBotAdapter = getOneBotAdapter();
            MessageEvent msg = oneBotAdapter.getMsg(Long.parseLong(chainForCommand.getReplySegment().getId()));
            Long userId = msg.getUserId();
            // 遍历后续消息段
            for (MessageSegment messageSeg : chainForCommand.getContent()) {
                // 如果发现了与回复消息相匹配的 @ 消息
                if (messageSeg.typeOf(MessageSegmentTypes.AT) &&
                        ((AtSegment) messageSeg).getQq().equals(String.valueOf(userId))) {
                    // 将此 @ 消息删除
                    chainForCommand.getContent().remove(messageSeg);
                    break;
                }
            }
        }
        return chainForCommand;
    }

    // 从 SpringContextUtil 中获取 OneBotAdapter
    private static OneBotAdapter getOneBotAdapter() {
        return SpringContextUtil.getBean(OneBotAdapter.class);
    }

    // 获取 BotApp
    private static BotApp getBotApp() {
        return SpringContextUtil.getBean(BotApp.class);
    }

    private static CommandMatched notMatch() {
        CommandMatched matched = new CommandMatched();
        matched.setMatchSuccess(false);
        return matched;
    }
}
