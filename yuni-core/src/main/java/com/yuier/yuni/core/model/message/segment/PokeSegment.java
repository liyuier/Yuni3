package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.PokeData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: PokeSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:50
 * @description: 戳一戳消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class PokeSegment extends MessageSegment {

    private PokeData data;

    public PokeSegment() {
        super("poke");
    }

    private Long getQq() {
        return data != null ? data.getQq() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
