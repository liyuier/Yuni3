package com.yuier.yuni.core.event.matched;

import com.yuier.yuni.core.enums.CommandArgRequireType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.AtSegment;
import com.yuier.yuni.core.model.message.segment.ImageSegment;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数匹配结果。
 *
 * <p>单值参数使用 {@code asXxx()} 方法；变长参数使用 {@code asXxxList()} 方法。</p>
 */
@Data
@NoArgsConstructor
public class ArgResult {

    /** 参数名 */
    private String name;

    /** 参数类型 */
    private CommandArgRequireType type = CommandArgRequireType.PLAIN;

    /** 单值参数的值（非变长时有效） */
    private MessageSegment value;

    /** 变长参数的值列表（变长时有效） */
    private List<MessageSegment> values = new ArrayList<>();

    /** 是否变长 */
    private boolean variadic;

    // ---- 构造 ----

    /** 单值构造 */
    public ArgResult(String name, CommandArgRequireType type, MessageSegment value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.variadic = false;
    }

    /** 变长构造 */
    public ArgResult(String name, CommandArgRequireType type, List<MessageSegment> values) {
        this.name = name;
        this.type = type;
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
        this.variadic = true;
    }

    // ---- 查询 ----

    public boolean isEmpty() {
        return variadic ? values.isEmpty() : (value == null);
    }

    public int size() {
        return variadic ? values.size() : (value == null ? 0 : 1);
    }

    // ---- 单值便捷方法 ----

    /** 取 value 的文本内容（非变长参数专用） */
    public String asText() {
        assertNotVariadic();
        if (value == null) return null;
        return value.typeOf(com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT)
                ? ((TextSegment) value).getText()
                : value.toString();
    }

    /** 取 value 为 ImageSegment（非变长参数专用） */
    public ImageSegment asImage() {
        assertNotVariadic();
        return (ImageSegment) value;
    }

    /** 取 value 为 AtSegment（非变长参数专用） */
    public AtSegment asAt() {
        assertNotVariadic();
        return (AtSegment) value;
    }

    /** 取 value 解析为 Long（非变长参数专用） */
    public Long asNumber() {
        assertNotVariadic();
        if (value == null) return null;
        String text = value.typeOf(com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT)
                ? ((TextSegment) value).getText()
                : value.toString();
        return Long.parseLong(text);
    }

    // ---- 变长便捷方法 ----

    /** 每个元素取文本内容（变长参数专用） */
    public List<String> asTextList() {
        assertVariadic();
        return values.stream()
                .map(seg -> seg.typeOf(com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT)
                        ? ((TextSegment) seg).getText()
                        : seg.toString())
                .collect(Collectors.toList());
    }

    /** 每个元素 cast 为 ImageSegment（变长参数专用） */
    public List<ImageSegment> asImageList() {
        assertVariadic();
        return values.stream()
                .map(seg -> (ImageSegment) seg)
                .collect(Collectors.toList());
    }

    /** 每个元素 cast 为 AtSegment（变长参数专用） */
    public List<AtSegment> asAtList() {
        assertVariadic();
        return values.stream()
                .map(seg -> (AtSegment) seg)
                .collect(Collectors.toList());
    }

    /** 每个元素解析为 Long（变长参数专用） */
    public List<Long> asNumberList() {
        assertVariadic();
        return values.stream()
                .map(seg -> {
                    String text = seg.typeOf(com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT)
                            ? ((TextSegment) seg).getText()
                            : seg.toString();
                    return Long.parseLong(text);
                })
                .collect(Collectors.toList());
    }

    // ---- 通用列表方法 ----

    /** 返回所有值的列表（单值参数返回单元素列表，变长参数返回全部值） */
    public List<MessageSegment> asList() {
        if (variadic) {
            return values;
        }
        return value == null ? Collections.emptyList() : Collections.singletonList(value);
    }

    // ---- 内部 ----

    private void assertNotVariadic() {
        if (variadic) {
            throw new IllegalStateException(
                    "参数 '" + name + "' 是变长参数，请使用 asXxxList() 方法取值");
        }
    }

    private void assertVariadic() {
        if (!variadic) {
            throw new IllegalStateException(
                    "参数 '" + name + "' 是单值参数，请使用 asXxx() 方法取值");
        }
    }
}
