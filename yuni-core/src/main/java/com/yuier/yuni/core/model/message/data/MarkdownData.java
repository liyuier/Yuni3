package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: MarkdownData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/20 17:50
 * @description: markdown 消息 data 字段
 */

@Getter
@Setter
@NoArgsConstructor
public class MarkdownData {
    private String data;

    // llob 实现的字段名为 content
    private String content;

    @Override
    public String toString() {
        return "[markdown消息<#" + this.data + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkdownData that = (MarkdownData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
