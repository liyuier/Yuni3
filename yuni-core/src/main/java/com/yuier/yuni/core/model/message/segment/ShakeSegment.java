package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ShakeData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ShakeSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:55
 * @description: 窗口抖动消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class ShakeSegment extends MessageSegment {

    private ShakeData data;

    public ShakeSegment() {
        super("shake");
    }
}
