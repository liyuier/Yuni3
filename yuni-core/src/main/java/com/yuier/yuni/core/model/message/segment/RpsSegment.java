package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.RpsData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: RpsSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:54
 * @description: 猜拳魔法表情
 */

@Getter
@Setter
@PolymorphicSubType
public class RpsSegment extends MessageSegment {

    private RpsData data;

    public RpsSegment() {
        super("rps");
    }

    public String getResult() {
        return data != null ? data.getResult() : null;
    }
}
