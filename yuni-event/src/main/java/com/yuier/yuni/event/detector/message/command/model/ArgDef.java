package com.yuier.yuni.event.detector.message.command.model;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令参数的完整定义。
 *
 * <p>单值参数：{@code ArgDef.required("target", "目标", CommandArgRequireType.IMAGE)}</p>
 * <p>变长参数：{@code ArgDef.variadic("files", "文件列表")} —— 语义等价于 {@code List<PLAIN>}</p>
 * <p>变长参数每个元素的类型必须一致，由 {@code type} 字段约束。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArgDef {

    /** 参数名 */
    private String name = "";

    /** 参数描述 */
    private String description = "";

    /** 期望的消息段类型，变长参数时约束每一个元素的类型 */
    private CommandArgRequireType type = CommandArgRequireType.PLAIN;

    /** 是否必须 */
    private boolean required = true;

    /** 是否变长（消费全部剩余 token） */
    private boolean variadic = false;

    /** 默认值文本（仅可选参数有意义） */
    private String defaultValue = null;

    // ---- 工厂方法 ----

    /** 必选参数（默认 PLAIN 类型） */
    public static ArgDef required(String name, String description) {
        return new ArgDef(name, description, CommandArgRequireType.PLAIN, true, false, null);
    }

    /** 必选参数（指定类型） */
    public static ArgDef required(String name, String description, CommandArgRequireType type) {
        return new ArgDef(name, description, type, true, false, null);
    }

    /** 可选参数（默认 PLAIN 类型） */
    public static ArgDef optional(String name, String description) {
        return new ArgDef(name, description, CommandArgRequireType.PLAIN, false, false, null);
    }

    /** 可选参数（指定类型） */
    public static ArgDef optional(String name, String description, CommandArgRequireType type) {
        return new ArgDef(name, description, type, false, false, null);
    }

    /** 可选参数（指定默认值） */
    public static ArgDef optional(String name, String description, String defaultValue) {
        return new ArgDef(name, description, CommandArgRequireType.PLAIN, false, false, defaultValue);
    }

    /** 变长参数（可选，默认 PLAIN 类型） */
    public static ArgDef variadic(String name, String description) {
        return new ArgDef(name, description, CommandArgRequireType.PLAIN, false, true, null);
    }

    /** 变长参数（可选，指定类型） */
    public static ArgDef variadic(String name, String description, CommandArgRequireType type) {
        return new ArgDef(name, description, type, false, true, null);
    }

    /** 必选变长参数（至少消费一个 token，默认 PLAIN 类型） */
    public static ArgDef requiredVariadic(String name, String description) {
        return new ArgDef(name, description, CommandArgRequireType.PLAIN, true, true, null);
    }

    /** 必选变长参数（至少消费一个 token，指定类型） */
    public static ArgDef requiredVariadic(String name, String description, CommandArgRequireType type) {
        return new ArgDef(name, description, type, true, true, null);
    }
}
