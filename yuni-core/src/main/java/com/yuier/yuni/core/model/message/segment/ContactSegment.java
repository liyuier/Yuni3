package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ContactData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ContactSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:29
 * @description: 推荐好友/群消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class ContactSegment extends MessageSegment {

    private ContactData data;

    public ContactSegment() {
        super("contact");
    }

    public String getType() {
        return data != null ? data.getType() : null;
    }

    public String getId() {
        return data != null ? data.getId() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
