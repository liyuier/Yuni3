package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ShareData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ShareSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:56
 * @description: 链接分享消息段 data 类
 */

@Getter
@Setter
@PolymorphicSubType
public class ShareSegment extends MessageSegment {

    private ShareData data;

    public ShareSegment() {
        super("share");
    }
}
