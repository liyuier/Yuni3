package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: VideoData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 21:46
 * @description: 短视频消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class VideoData {
    // 文件名
    private String file;
    // 图片 URL
    private String url;
    // 只在通过网络 URL 发送时有效，表示是否使用已缓存的文件，默认 1
    private String cache;
    // 只在通过网络 URL 发送时有效，表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认 1
    private String proxy;
    // 只在通过网络 URL 发送时有效，单位秒，表示下载网络文件的超时事件，默认不超时
    private String timeout;

    // 以下为 LLOneBot 自行添加字段
    private String fileSize;
    // 缩略图 URL
    private String thumb;
    // 图片名称
    private String name;
    // 文件本地路径
    private String path;

    @Override
    public String toString() {
        return "[视频" + "<file=" + this.file + "><url=" + this.url + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoData videoData = (VideoData) o;
        return Objects.equals(file, videoData.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}
