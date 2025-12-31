package com.yuier.yuni.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: HeartbeatEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.model.meta
 * @Date 2025/12/26 2:11
 * @description: 心跳事件
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatEvent {

    @JsonProperty("time")
    private Long time; // Unix timestamp (seconds)

    @JsonProperty("self_id")
    private Long selfId;

    @JsonProperty("post_type")
    private String postType;

    @JsonProperty("meta_event_type")
    private String metaEventType;

    @JsonProperty("status")
    private HeartbeatStatus status;

    @JsonProperty("interval")
    private Long interval; // 心跳间隔，单位：毫秒
}
