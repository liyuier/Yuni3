package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.JsonData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: JsonSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:43
 * @description: JSON 消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class JsonSegment extends MessageSegment {

    private JsonData data;

    public JsonSegment() {
        super("json");
    }

    public String getJsonData() {
        return data != null ? data.getData() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
