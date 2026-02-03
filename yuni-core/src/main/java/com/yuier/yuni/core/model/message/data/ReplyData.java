package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: ReplySegment
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:24
 * @description: 回复消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class ReplyData {
    // 回复时引用的消息 ID
    private String id;

    public ReplyData(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[回复<消息id="+ this.id + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyData replyData = (ReplyData) o;
        return Objects.equals(id, replyData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
