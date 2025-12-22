package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.FaceData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: FaceSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:36
 * @description: 表情消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class FaceSegment extends MessageSegment {

    private FaceData data;

    public FaceSegment() {
        super("face");
    }

    private String getId() {
        return data != null ? data.getId() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }

}
