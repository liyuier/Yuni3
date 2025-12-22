package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: ShareData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:09
 * @description: 链接分享消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class ShareData {
    private String url;
    private String title;
    // 发送时可选，内容描述
    private String content;
    // 发送时可选，图片 URL
    private String image;

    @Override
    public String toString() {
        return "[链接分享<url=" + this.url + "><title=" + this.title + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareData shareData = (ShareData) o;
        return Objects.equals(url, shareData.url) && Objects.equals(title, shareData.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, title);
    }
}
