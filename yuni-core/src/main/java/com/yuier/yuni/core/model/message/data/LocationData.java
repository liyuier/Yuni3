package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: LocationData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:13
 * @description: 位置消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class LocationData {
    // 纬度
    private String lat;
    // 经度
    private String lon;
    // 发送时可选，标题
    private String title;
    // 发送时可选，内容描述
    private String content;

    @Override
    public String toString() {
        return "[位置消息<纬度=" + this.lat + "><经度=" + this.lon + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationData that = (LocationData) o;
        return Objects.equals(lat, that.lat) && Objects.equals(lon, that.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
