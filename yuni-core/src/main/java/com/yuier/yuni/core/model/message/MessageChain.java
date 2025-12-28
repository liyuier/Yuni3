package com.yuier.yuni.core.model.message;

import com.yuier.yuni.core.constants.MessageSegmentTypes;
import com.yuier.yuni.core.model.message.segment.TextSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.yuier.yuni.core.constants.MessageSegmentTypes.*;
import static com.yuier.yuni.core.constants.SystemConstants.FIRST_INDEX;

/**
 * @Title: MessageChain
 * @Author yuier
 * @Package com.yuier.yuni.core.model.message.chain
 * @Date 2025/12/22 17:23
 * @description: 消息链
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageChain {

    private List<MessageSegment> content = new ArrayList<>();

    public MessageChain(String text) {
        this.addTextSegment(text);
    }

    // 传入 MessageSegment 的构造函数
    public MessageChain(MessageSegment messageSegment) {
        content = new ArrayList<>();
        this.addSegment(messageSegment);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (MessageSegment seg : content) {
            str.append(seg.toString());
        }
        return str.toString();
    }

    public MessageSegment getSegment(int index) {
        return content.get(index);
    }

    /**
     * 是否以文本消息段开头
     * @return 是否以文本消息段开头
     */
    public Boolean startWithTextData() {
        return getSegment(FIRST_INDEX).typeOf(TEXT) &&  // 第一个消息段是文本消息段
                !((TextSegment) getSegment(FIRST_INDEX)).getText().trim().isEmpty();  // 文本消息段内容不为空
    }

    public MessageChain addSegment(MessageSegment segment) {
        content.add(segment);
        return this;
    }

    public MessageChain addTextSegment(String text) {
        return addSegment(new TextSegment(text));
    }

    public boolean startWithReplyData() {
        return content.get(FIRST_INDEX).typeOf(REPLY);
    }

    public MessageSegment removeSegment(int index) {
        if (index < 0 || index >= content.size()) {
            throw new IndexOutOfBoundsException("索引越界");
        }
        return content.remove(index);
    }

    public Boolean containsString(String str) {
        for (MessageSegment seg : content) {
            if (seg.typeOf(TEXT) && ((TextSegment) seg).getText().contains(str)) {
                return true;
            }
        }
        return false;
    }

    public MessageChain addAll(List<MessageSegment> messageSegmentList) {
        content.addAll(messageSegmentList);
        return this;
    }

    public Boolean containsForwardMessage() {
        for (MessageSegment seg : content) {
            if (seg.typeOf(FORWARD)) {
                return true;
            }
        }
        return false;
    }

    // 是否包含 MessageSegmentTypes 中定义的某个消息段
    public Boolean contains(String type) {
        if (!MessageSegmentTypes.ALL.contains(type)) {
            return false;
        }
        for (MessageSegment seg : content) {
            if (seg.typeOf(type)) {
                return true;
            }
        }
        return false;
    }
}
