package com.yuier.yuni.core.api.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Title: GroupAll
 * @Author yuier
 * @Package com.yuier.yuni.core.api.group
 * @Date 2025/12/24 18:43
 * @description:
 */

@Data
@NoArgsConstructor
public class GroupAll {

    @JsonProperty("groupCode")
    private String groupCode;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("ownerUid")
    private String ownerUid;

    @JsonProperty("groupFlag")
    private Long groupFlag;

    @JsonProperty("groupFlagExt")
    private Long groupFlagExt;

    @JsonProperty("maxMemberNum")
    private Integer maxMemberNum;

    @JsonProperty("memberNum")
    private Integer memberNum;

    @JsonProperty("groupOption")
    private Integer groupOption;

    @JsonProperty("classExt")
    private Integer classExt;

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("fingerMemo")
    private String fingerMemo;

    @JsonProperty("groupQuestion")
    private String groupQuestion;

    @JsonProperty("certType")
    private Integer certType;

    @JsonProperty("shutUpAllTimestamp")
    private Long shutUpAllTimestamp;

    @JsonProperty("shutUpMeTimestamp")
    private Long shutUpMeTimestamp;

    @JsonProperty("groupTypeFlag")
    private Integer groupTypeFlag;

    @JsonProperty("privilegeFlag")
    private Long privilegeFlag;

    @JsonProperty("groupSecLevel")
    private Integer groupSecLevel;

    @JsonProperty("groupFlagExt3")
    private Long groupFlagExt3;

    @JsonProperty("isConfGroup")
    private Integer isConfGroup;

    @JsonProperty("isModifyConfGroupFace")
    private Integer isModifyConfGroupFace;

    @JsonProperty("isModifyConfGroupName")
    private Integer isModifyConfGroupName;

    @JsonProperty("noFigerOpenFlag")
    private Integer noFigerOpenFlag;

    @JsonProperty("noCodeFingerOpenFlag")
    private Integer noCodeFingerOpenFlag;

    @JsonProperty("groupFlagExt4")
    private Long groupFlagExt4;

    @JsonProperty("groupMemo")
    private String groupMemo;

    @JsonProperty("cmdUinMsgSeq")
    private Long cmdUinMsgSeq;

    @JsonProperty("cmdUinJoinTime")
    private Long cmdUinJoinTime;

    @JsonProperty("cmdUinUinFlag")
    private Integer cmdUinUinFlag;

    @JsonProperty("cmdUinMsgMask")
    private Integer cmdUinMsgMask;

    @JsonProperty("groupSecLevelInfo")
    private Integer groupSecLevelInfo;

    @JsonProperty("cmdUinPrivilege")
    private Integer cmdUinPrivilege;

    @JsonProperty("cmdUinFlagEx2")
    private Long cmdUinFlagEx2;

    @JsonProperty("appealDeadline")
    private Long appealDeadline;

    @JsonProperty("remarkName")
    private String remarkName;

    @JsonProperty("isTop")
    private Boolean isTop;

    @JsonProperty("richFingerMemo")
    private String richFingerMemo;

    @JsonProperty("groupAnswer")
    private String groupAnswer;

    @JsonProperty("joinGroupAuth")
    private String joinGroupAuth;

    @JsonProperty("isAllowModifyConfGroupName")
    private Integer isAllowModifyConfGroupName;
}