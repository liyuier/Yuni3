package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: XmlData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:35
 * @description: xml 消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class XmlData {
    // XML 内容
    private String data;

    @Override
    public String toString() {
        return "[XML消息<" + this.data + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlData xmlData = (XmlData) o;
        return Objects.equals(data, xmlData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
