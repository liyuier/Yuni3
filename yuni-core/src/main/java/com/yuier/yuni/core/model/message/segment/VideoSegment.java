package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.VideoData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: VideoSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 2:01
 * @description: 短视频消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class VideoSegment extends MessageSegment {

    private VideoData data;

    public VideoSegment() {
        super("video");
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
}
