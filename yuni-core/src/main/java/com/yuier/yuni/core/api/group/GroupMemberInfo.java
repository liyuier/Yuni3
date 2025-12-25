package com.yuier.yuni.core.api.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Title: GroupMemberInfo
 * @Author yuier
 * @Package com.yuier.yuni.core.api.group
 * @Date 2025/12/26 1:06
 * @description:
 */

@Data
@NoArgsConstructor
public class GroupMemberInfo {

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("card")
    private String card;

    @JsonProperty("card_or_nickname")
    private String cardOrNickname;

    @JsonProperty("sex")
    private String sex; // 通常为 "male" / "female" / "unknown"

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("area")
    private String area;

    @JsonProperty("level")
    private String level; // 群内等级名称（如 "群主", "管理员" 等字符串形式，注意与 role 区分）

    @JsonProperty("qq_level")
    private Integer qqLevel;

    @JsonProperty("join_time")
    private Long joinTime; // Unix timestamp (seconds)

    @JsonProperty("last_sent_time")
    private Long lastSentTime; // Unix timestamp (seconds)

    @JsonProperty("title_expire_time")
    private Long titleExpireTime; // Unix timestamp (seconds)

    @JsonProperty("unfriendly")
    private Boolean unfriendly;

    @JsonProperty("card_changeable")
    private Boolean cardChangeable;

    @JsonProperty("is_robot")
    private Boolean isRobot;

    @JsonProperty("shut_up_timestamp")
    private Long shutUpTimestamp; // Unix timestamp (seconds)

    @JsonProperty("role")
    private String role; // 通常为 "owner" / "admin" / "member"

    @JsonProperty("title")
    private String title; // 群头衔
}
