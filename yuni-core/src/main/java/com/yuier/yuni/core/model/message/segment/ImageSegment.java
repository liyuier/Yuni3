package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.ImageData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: ImageSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:43
 * @description: 图片消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class ImageSegment extends MessageSegment {

    private ImageData data;

    public ImageSegment() {
        super("image");
    }

    public String getFile() {
        return data != null ? data.getFile() : null;
    }

    public ImageSegment setFile(String file) {
        if (data == null) {
            data = new ImageData(file);
        } else {
            data.setFile(file);
        }
        return this;
    }

    public String getName() {
        return data != null ? data.getName() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
