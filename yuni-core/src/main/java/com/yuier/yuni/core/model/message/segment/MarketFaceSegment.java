package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.MarketFaceData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: MarketFaceSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:47
 * @description: 商城表情消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class MarketFaceSegment extends MessageSegment {

    private MarketFaceData data;

    public MarketFaceSegment() {
        super("mface");
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
