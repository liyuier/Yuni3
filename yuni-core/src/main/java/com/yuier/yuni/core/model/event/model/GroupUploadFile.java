package com.yuier.yuni.core.model.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupUploadFile
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event.model
 * @Date 2025/12/29 23:44
 * @description:
 */

@Data
@NoArgsConstructor
public class GroupUploadFile {

    // 文件 ID
    @JsonProperty("id")
    private String id;

    // 文件名
    @JsonProperty("name")
    private String name;

    // 文件大小
    @JsonProperty("size")
    private Long size;

    // 业务 ID
    @JsonProperty("busid")
    private Long busid;
}
