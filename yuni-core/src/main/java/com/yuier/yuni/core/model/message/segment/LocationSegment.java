package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.LocationData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: LocationSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:44
 * @description: 位置消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class LocationSegment extends MessageSegment {

    private LocationData data;

    public LocationSegment() {
        super("location");
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
