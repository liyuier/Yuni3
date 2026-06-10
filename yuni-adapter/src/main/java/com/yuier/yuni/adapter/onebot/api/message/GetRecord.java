package com.yuier.yuni.adapter.onebot.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GetRecord
 * @Author yuier
 * @Package com.yuier.yuni.core.api.message
 * @Date 2025/12/26 1:19
 * @description: 获取语音消息详情
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRecord {

    @JsonProperty("file")
    private String file; // 本地临时文件路径（如 .wav）

    @JsonProperty("url")
    private String url; // 原始语音文件路径（如 .amr）

    @JsonProperty("file_size")
    private String fileSize; // 文件大小（字符串形式，单位：字节）

    @JsonProperty("file_name")
    private String fileName; // 文件名
}
