package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: JsonData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:37
 * @description: Json 消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class JsonData {
    private String data;

    @Override
    public String toString() {
        return "[JSON消息<" + this.data + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonData jsonData = (JsonData) o;
        return Objects.equals(data, jsonData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
