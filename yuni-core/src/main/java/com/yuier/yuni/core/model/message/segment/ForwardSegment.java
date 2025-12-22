package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ForwardData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ForwardSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:42
 * @description: 转发消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class ForwardSegment extends MessageSegment {

    private ForwardData data;

    public ForwardSegment() {
        super("forward");
    }

    private String getId() {
        return data != null ? data.getId() : null;
    }
}
