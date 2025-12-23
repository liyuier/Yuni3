package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.NodeData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: NodeSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:49
 * @description: 合并转发消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class NodeSegment extends MessageSegment {

    private NodeData data;

    public NodeSegment() {
        super("node");
    }

    public String getId() {
        return data != null ? data.getId() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
