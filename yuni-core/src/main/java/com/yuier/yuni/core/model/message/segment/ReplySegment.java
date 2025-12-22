package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ReplyData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ReplySegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:53
 * @description: 回复消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class ReplySegment extends MessageSegment {

    private ReplyData data;

    public ReplySegment() {
        super("reply");
    }

    private String getId() {
        return data != null ? data.getId() : null;
    }
}
