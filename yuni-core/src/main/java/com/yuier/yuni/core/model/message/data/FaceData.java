package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: FaceData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 21:38
 * @description: QQ 表情消息 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class FaceData {
    private String id;

    @Override
    public String toString() {
        return "[QQ表情#" + this.id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaceData faceData = (FaceData) o;
        return Objects.equals(id, faceData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
