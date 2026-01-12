package com.yuier.yuni.event.detector.message.command.model.matched;

import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Title: CommandMatched
 * @Author yuier
 * @Package com.yuier.yuni.event.model.message.detector.command
 * @Date 2025/12/23 1:03
 * @description: 匹配出的结果
 */

@Data
@NoArgsConstructor
public class CommandMatched {

    // 命令头
    private String head;

    // 以参数的 name 为 key，匹配出的参数为 value
    private Map<String, CommandArgMatched> argsMatchedout;

    // 以选项的 flag 为 key，匹配出的选项为 value
    private Map<String, CommandOptionMatched> optionsMatchedout;

    // 是否匹配成功
    private Boolean matchSuccess = false;

    // 判断是否存在参数
    public Boolean hasArg(String name) {
        return argsMatchedout.containsKey(name);
    }

    // 根据参数名获取参数
    public CommandArgMatched getArg(String name) {
        return argsMatchedout.get(name);
    }

    public MessageSegment getArgValue(String name) {
        return getArg(name).getValue();
    }

    // 判断是否存在选项
    public Boolean hasOption(String flag) {
        return optionsMatchedout.containsKey(flag);
    }

    // 根据选项 flag 获取选项
    public CommandOptionMatched getOption(String flag) {
        return optionsMatchedout.get(flag);
    }

    // 判断某选项是否存在必选参数
    public Boolean optionHasRequiredArg(String flag) {
        return hasOption(flag) && getOption(flag).hasRequiredArg();
    }

    // 获取某选项的必选参数
    public CommandArgMatched getOptionRequiredArg(String flag) {
        if (!hasOption(flag)) {
            return null;
        }
        return getOption(flag).getRequiredArg();
    }

    // 获取某选项的必选参数的值
    public MessageSegment getOptionRequiredArgValue(String flag) {
        if (!hasOption(flag)) {
            return null;
        }
        return getOption(flag).getRequiredArgValue();
    }

    // 判断某选项是否存在可选参数
    public Boolean optionHasOptionalArg(String optionFlag) {
        return hasOption(optionFlag) && getOption(optionFlag).hasOptionalArg();
    }

    // 获取某选项的可选参数
    public CommandArgMatched getOptionOptionalArg(String flag) {
        if (!hasOption(flag)) {
            return null;
        }
        return getOption(flag).getOptionalArg();
    }

    // 获取某选项的可选参数的值
    public MessageSegment getOptionOptionalArgValue(String flag) {
        if (!hasOption(flag)) {
            return null;
        }
        return getOption(flag).getOptionalArgValue();
    }

    // 判断某选项是否存在参数
    public Boolean optionHasArg(String flag) {
        return hasOption(flag) && (optionHasRequiredArg(flag) || optionHasOptionalArg(flag));
    }
}
