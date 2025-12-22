package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.AnonymousData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: AnonymousSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:20
 * @description: 匿名消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class AnonymousSegment extends MessageSegment {

    private AnonymousData data;

    public AnonymousSegment() {
        super();
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
