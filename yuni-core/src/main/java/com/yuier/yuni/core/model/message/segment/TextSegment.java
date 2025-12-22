package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.TextData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: TextSegment
 * @Author yuier
 * @Package com.yuier.yuni.core.model.message.segment
 * @Date 2025/12/22 5:19
 * @description: 文本消息段实体类
 */

@Getter
@Setter
@PolymorphicSubType
public class TextSegment extends MessageSegment {

    private TextData data;

    public TextSegment() {
        super("text");
    }

    public TextSegment(String text) {
        super("text");
        this.data = new TextData(text);
    }

    public String getText() {
        return data != null ? data.getText() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
