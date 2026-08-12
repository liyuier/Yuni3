package com.yuier.yuni.event.detector.message.command;

import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.segment.ReplySegment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 命令匹配器的输入 token 列表 + 已提取的回复消息。
 *
 * <p>QQ 客户端的特性：回复消息段必定位于消息链之首，且至多一个。开发者无法以
 * "回复在当前位置" 的模型来定义 {@code REPLY} 类型参数。因此 tokenize 阶段
 * 将回复单独抽离存储，匹配时不由 token 流位置消耗，而是从这里读取。</p>
 */
@Getter
public class CommandTokens {

    /** 已 tokenize 的消息段列表（不含回复消息段） */
    private final List<MessageSegment> tokens;

    /** 回复消息段（已从 token 流中提取），可为 null */
    private final ReplySegment reply;

    public CommandTokens(List<MessageSegment> tokens, ReplySegment reply) {
        this.tokens = Collections.unmodifiableList(tokens);
        this.reply = reply;
    }

    public boolean hasReply() {
        return reply != null;
    }

    public MessageSegment get(int index) {
        return tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public List<MessageSegment> subList(int fromIndex, int toIndex) {
        return new ArrayList<>(tokens.subList(fromIndex, toIndex));
    }
}
