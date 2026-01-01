package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.AtData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: AtSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:22
 * @description: @ 消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class AtSegment extends MessageSegment {

    private AtData data;

    public AtSegment() {
        super("at");
    }

    public AtSegment(String qq) {
        super("at");
        this.data = new AtData(qq);
    }

    public String getQq() {
        return data != null ? data.getQq() : null;
    }

    public String getName() {
        return data != null ? data.getName() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
