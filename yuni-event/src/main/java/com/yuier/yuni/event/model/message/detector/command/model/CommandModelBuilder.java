package com.yuier.yuni.event.model.message.detector.command.model;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.enums.UserPermission;


/**
 * CommandModel 构建器
 */
public class CommandModelBuilder {
    private CommandModel commandModel;

    private CommandModelBuilder(String head) {
        this.commandModel = new CommandModel();
        this.commandModel.setHead(head);
    }

    public static CommandModelBuilder create(String head) {
        return new CommandModelBuilder(head);
    }

    /**
     * 添加必选参数
     */
    public CommandModelBuilder addRequiredArg(String name, String description) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        commandModel.getRequiredArgs().add(arg);
        return this;
    }

    /**
     * 添加必选参数（指定类型）
     */
    public CommandModelBuilder addRequiredArg(String name, String description, CommandArgRequireType requiredType) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        arg.setRequiredType(requiredType);
        commandModel.getRequiredArgs().add(arg);
        return this;
    }

    /**
     * 添加可选参数
     */
    public CommandModelBuilder addOptionalArg(String name, String description) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        commandModel.getOptionalArgs().add(arg);
        return this;
    }

    /**
     * 添加可选参数（指定类型）
     */
    public CommandModelBuilder addOptionalArg(String name, String description, CommandArgRequireType requiredType) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        arg.setRequiredType(requiredType);
        commandModel.getOptionalArgs().add(arg);
        return this;
    }

    /**
     * 添加选项（无参数）
     */
    public CommandModelBuilder addOption(String flag) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);
        commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带必选参数的选项
     */
    public CommandModelBuilder addOptionWithRequiredArg(String flag, String argName, String argDescription) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        option.setRequiredArg(arg);

        commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带必选参数的选项（指定类型）
     */
    public CommandModelBuilder addOptionWithRequiredArg(String flag, String argName, String argDescription, CommandArgRequireType argType) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        arg.setRequiredType(argType);
        option.setRequiredArg(arg);

        commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带可选参数的选项
     */
    public CommandModelBuilder addOptionWithOptionalArg(String flag, String argName, String argDescription) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        option.setOptionalArg(arg);

        commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带可选参数的选项（指定类型）
     */
    public CommandModelBuilder addOptionWithOptionalArg(String flag, String argName, String argDescription, CommandArgRequireType argType) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        arg.setRequiredType(argType);
        option.setOptionalArg(arg);

        commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 设置选项权限
     */
    public CommandModelBuilder setOptionPermission(String flag, UserPermission permission) {
        CommandOption option = commandModel.getOptions().stream()
                .filter(opt -> opt.getFlag().equals(flag))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("选项 '" + flag + "' 不存在"));
        option.setPermission(permission);
        return this;
    }

    /**
     * 设置命令权限
     */
    public CommandModelBuilder setPermission(UserPermission permission) {
        commandModel.setPermission(permission);
        return this;
    }

    /**
     * 验证参数名称唯一性
     */
    private void validateArgNameUniqueness(String name) {
        if (commandModel.hasArg(name)) {
            throw new IllegalArgumentException("参数名称 '" + name + "' 已存在");
        }
    }

    /**
     * 验证选项标识符唯一性
     */
    private void validateOptionFlagUniqueness(String flag) {
        if (commandModel.hasOption(flag)) {
            throw new IllegalArgumentException("选项标识符 '" + flag + "' 已存在");
        }
    }

    /**
     * 构建 CommandModel 实例
     */
    public CommandModel build() {
        return commandModel;
    }

    private void example() {
        // 构建一个简单的 echo 命令
        CommandModel echoCommand = CommandModelBuilder.create("echo")
                .addRequiredArg("message", "要输出的消息内容")
                .setPermission(UserPermission.USER)
                .build();

        // 构建一个带有选项的 git commit 命令
        CommandModel gitCommand = CommandModelBuilder.create("git")
                .addRequiredArg("command", "git 子命令")
                .addOptionalArg("args", "子命令参数")
                .addOptionWithRequiredArg("-m", "message", "提交信息")
                .addOption("-a")
                .setOptionPermission("-a", UserPermission.ADMIN)
                .setPermission(UserPermission.USER)
                .build();
    }
}
