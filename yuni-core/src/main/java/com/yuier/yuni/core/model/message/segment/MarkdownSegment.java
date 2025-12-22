package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.MarkdownData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: MarkdownSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:45
 * @description: markdown 消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class MarkdownSegment extends MessageSegment {

    private MarkdownData data;

    public MarkdownSegment() {
        super("markdown");
    }

    public String getContent() {
        return data != null ? data.getContent() : null;
    }
}
