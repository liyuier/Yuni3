package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.XmlData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: XmlSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 2:02
 * @description: XML 消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class XmlSegment extends MessageSegment {

    private XmlData data;

    public XmlSegment() {
        super("xml");
    }

    private String getXmlData() {
        return data != null ? data.getData() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
