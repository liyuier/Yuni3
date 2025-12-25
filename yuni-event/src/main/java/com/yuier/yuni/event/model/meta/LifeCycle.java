package com.yuier.yuni.event.model.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: LifeCycle
 * @Author yuier
 * @Package com.yuier.yuni.event.model.meta
 * @Date 2025/12/26 3:22
 * @description: 生命周期事件
 */

@Data
@NoArgsConstructor
public class LifeCycle {

    @JsonProperty("time")
    private Long time; // Unix timestamp (seconds)

    @JsonProperty("self_id")
    private Long selfId;

    @JsonProperty("post_type")
    private String postType;

    @JsonProperty("meta_event_type")
    private String metaEventType;

    @JsonProperty("sub_type")
    private String subType;
}
