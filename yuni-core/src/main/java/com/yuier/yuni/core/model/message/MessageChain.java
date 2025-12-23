package com.yuier.yuni.core.model.message;

import com.yuier.yuni.core.model.message.segment.TextSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.yuier.yuni.core.constants.MessageSegmentTypes.REPLY;
import static com.yuier.yuni.core.constants.MessageSegmentTypes.TEXT;
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

    public void addSegment(MessageSegment segment) {
        content.add(segment);
    }

    public void addTextSegment(String text) {
        addSegment(new TextSegment(text));
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
}
