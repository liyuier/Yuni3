package com.yuier.yuni.event.model.message.detector.command.model;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.enums.UserPermission;


public class CommandModelBuilder {
    private CommandModel commandModel;

    private CommandModelBuilder(String head) {
        this.commandModel = new CommandModel();
        this.commandModel.setHead(head);
    }

    /**
     * 创建命令构建器
     * @param head 命令头
     * @return 命令构建器实例
     */
    public static CommandModelBuilder create(String head) {
        return new CommandModelBuilder(head);
    }

    /**
     * 添加必选参数
     * @param name 参数名称
     * @param description 参数描述
     * @return 构建器实例
     */
    public CommandModelBuilder addRequiredArg(String name, String description) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        this.commandModel.getRequiredArgs().add(arg);
        return this;
    }

    /**
     * 添加必选参数（指定消息段类型）
     * @param name 参数名称
     * @param description 参数描述
     * @param requiredType 消息段类型
     * @return 构建器实例
     */
    public CommandModelBuilder addRequiredArg(String name, String description, CommandArgRequireType requiredType) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        arg.setRequiredType(requiredType);
        this.commandModel.getRequiredArgs().add(arg);
        return this;
    }

    /**
     * 添加可选参数
     * @param name 参数名称
     * @param description 参数描述
     * @return 构建器实例
     */
    public CommandModelBuilder addOptionalArg(String name, String description) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        this.commandModel.getOptionalArgs().add(arg);
        return this;
    }

    /**
     * 添加可选参数（指定消息段类型）
     * @param name 参数名称
     * @param description 参数描述
     * @param requiredType 消息段类型
     * @return 构建器实例
     */
    public CommandModelBuilder addOptionalArg(String name, String description, CommandArgRequireType requiredType) {
        validateArgNameUniqueness(name);
        CommandArg arg = new CommandArg();
        arg.setName(name);
        arg.setDescription(description);
        arg.setRequiredType(requiredType);
        this.commandModel.getOptionalArgs().add(arg);
        return this;
    }

    /**
     * 添加选项
     * @param flag 选项标识符
     * @return 构建器实例
     */
    public CommandModelBuilder addOption(String flag) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);
        this.commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带参数的选项
     * @param flag 选项标识符
     * @param argName 选项参数名称
     * @param argDescription 选项参数描述
     * @return 构建器实例
     */
    public CommandModelBuilder addOptionWithArg(String flag, String argName, String argDescription) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        option.setArg(arg);

        this.commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 添加带参数的选项（指定消息段类型）
     * @param flag 选项标识符
     * @param argName 选项参数名称
     * @param argDescription 选项参数描述
     * @param argType 消息段类型
     * @return 构建器实例
     */
    public CommandModelBuilder addOptionWithArg(String flag, String argName, String argDescription, CommandArgRequireType argType) {
        validateOptionFlagUniqueness(flag);
        CommandOption option = new CommandOption();
        option.setFlag(flag);

        CommandArg arg = new CommandArg();
        arg.setName(argName);
        arg.setDescription(argDescription);
        arg.setRequiredType(argType);
        option.setArg(arg);

        this.commandModel.getOptions().add(option);
        return this;
    }

    /**
     * 设置选项权限
     * @param flag 选项标识符
     * @param permission 权限级别
     * @return 构建器实例
     */
    public CommandModelBuilder setOptionPermission(String flag, UserPermission permission) {
        CommandOption option = this.commandModel.getOptions().stream()
                .filter(opt -> opt.getFlag().equals(flag))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("选项 '" + flag + "' 不存在"));
        option.setPermission(permission);
        return this;
    }

    /**
     * 设置命令权限
     * @param permission 权限级别
     * @return 构建器实例
     */
    public CommandModelBuilder setPermission(UserPermission permission) {
        this.commandModel.setPermission(permission);
        return this;
    }

    /**
     * 验证参数名称唯一性
     * @param name 参数名称
     */
    private void validateArgNameUniqueness(String name) {
        boolean existsInRequired = commandModel.getRequiredArgs().stream()
                .anyMatch(arg -> arg.getName().equals(name));
        boolean existsInOptional = commandModel.getOptionalArgs().stream()
                .anyMatch(arg -> arg.getName().equals(name));

        if (existsInRequired || existsInOptional) {
            throw new IllegalArgumentException("参数名称 '" + name + "' 已存在");
        }
    }

    /**
     * 验证选项标识符唯一性
     * @param flag 选项标识符
     */
    private void validateOptionFlagUniqueness(String flag) {
        boolean exists = commandModel.getOptions().stream()
                .anyMatch(option -> option.getFlag().equals(flag));

        if (exists) {
            throw new IllegalArgumentException("选项标识符 '" + flag + "' 已存在");
        }
    }

    /**
     * 构建命令模型
     * @return 完整的命令模型
     */
    public CommandModel build() {
        return this.commandModel;
    }

    private void example() {
        // 构建一个简单的命令
        CommandModel simpleCommand = CommandModelBuilder.create("help")
            .addOptionalArg("command", "要查看帮助的命令")
            .setPermission(UserPermission.USER)
            .build();
    }
}