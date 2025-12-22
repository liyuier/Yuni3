package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.DiceData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: DiceSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:33
 * @description: 掷骰子消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class DiceSegment extends MessageSegment {

    private DiceData data;

    public DiceSegment() {
        super("dice");
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
