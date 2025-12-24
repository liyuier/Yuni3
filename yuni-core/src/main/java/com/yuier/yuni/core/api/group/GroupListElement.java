package com.yuier.yuni.core.api.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupListElement
 * @Author yuier
 * @Package com.yuier.yuni.core.api.group
 * @Date 2025/12/24 23:50
 * @description: get_group_list 响应的数组元素
 */

@Data
@NoArgsConstructor
public class GroupListElement {
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

    @JsonProperty("remark_name")
    private String remarkName;
}
