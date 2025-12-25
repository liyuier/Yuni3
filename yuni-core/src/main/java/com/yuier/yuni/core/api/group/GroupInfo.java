package com.yuier.yuni.core.api.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Title: GroupInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.api.group
 * @Date 2025/12/24 18:44
 * @description: get_group_info API 的响应
 */

@Data
@NoArgsConstructor
public class GroupInfo {

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_memo")
    private String groupMemo;

    @JsonProperty("group_create_time")
    private Long groupCreateTime; // Unix timestamp (seconds)

    @JsonProperty("member_count")
    private Integer memberCount;

    @JsonProperty("max_member_count")
    private Integer maxMemberCount;

    @JsonProperty("groupAll")
    private GroupAll groupAll;

}
