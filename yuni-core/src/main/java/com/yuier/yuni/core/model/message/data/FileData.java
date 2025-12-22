package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: FileData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/20 17:55
 * @description: 文件 data 字段
 */

@Getter
@Setter
@NoArgsConstructor
public class FileData {
    /**
     * 文件路径
     */
    private String file;
    /**
     * 发送时支持自定义显示文件名
     */
    private String name;

    /* 下面疑似是 llob 自己添加的字段 */

    // 文件 URL
    private String url;

    // 文件本地路径
    private String path;

    // 文件大小（字节）
    private String fileSize;

    // 文件 UUID
    private String fileId;

    // 缩略图 URL
    private String thumb;

    public FileData(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "[文件" + "<file=" + this.file + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileData fileData = (FileData) o;
        return Objects.equals(file, fileData.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}
