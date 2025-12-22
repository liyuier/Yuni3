package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: MarketFaceData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/20 17:46
 * @description: 商城表情 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class MarketFaceData {
    private String summary;
    private String url;
    private String emojiId;
    private String emojiPackageId;
    private String key;

    @Override
    public String toString() {
        return "[商城表情#" + this.summary + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketFaceData that = (MarketFaceData) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
