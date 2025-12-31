package com.yuier.yuni.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: HeartbeatStatus
 * @Author yuier
 * @Package com.yuier.yuni.event.model.meta
 * @Date 2025/12/26 2:12
 * @description: 心跳状态
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatStatus {

    @JsonProperty("online")
    private Boolean online;

    @JsonProperty("good")
    private Boolean good;
}
