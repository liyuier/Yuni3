package com.yuier.yuni.core.model.message.segment;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.message.MessageSegment;
import com.yuier.yuni.core.model.message.data.FileData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Title: FileSegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.event.message.chain.seg
 * @Date 2024/11/11 1:40
 * @description: 文件消息段
 */

@Getter
@Setter
@PolymorphicSubType
public class FileSegment extends MessageSegment {

    private FileData data;

    public FileSegment() {
        super("file");
    }

    public String getFile() {
        return data != null ? data.getFile() : null;
    }

    public String getName() {
        return data != null ? data.getName() : null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "";
    }
}
