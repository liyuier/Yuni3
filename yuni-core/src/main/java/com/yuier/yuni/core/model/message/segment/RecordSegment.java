package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.RecordData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: RecordSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:53
 * @description: 语音消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class RecordSegment extends MessageSegment {

    private RecordData data;

    public RecordSegment() {
        super("record");
    }

    private String getFile() {
        return data != null ? data.getFile() : null;
    }

    private String getName() {
        return data != null ? data.getName() : null;
    }

    private String getPath() {
        return data != null ? data.getPath() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
