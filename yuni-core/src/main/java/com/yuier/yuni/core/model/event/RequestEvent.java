package com.yuier.yuni.core.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuier.yuni.core.anno.PolymorphicSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: RequestEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
 * @Date 2025/12/29 17:07
 * @description: 请求事件
 */

@Getter
@Setter
@NoArgsConstructor
@PolymorphicSubType
public class RequestEvent extends OneBotEvent {
    /**
     * 请求类型
     *   - friend 添加好友请求
     *   - group 加群请求 / 邀请登录号入群请求
     */
    @JsonProperty("request_type")
    private String requestType;

    // 验证信息
    private String comment;

    // 发送请求的 QQ 号
    @JsonProperty("user_id")
    private Long userId;

    // 请求 flag，在调用处理请求的 API 时需要
    @JsonProperty("flag")
    private String flag;

    /* 加群 / 邀请登录号入群 请求的字段 */

    /**
     * 请求子类型
     * - add 加群请求
     * - invite 邀请登录号入群
     */
    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("group_id")
    private Long groupId;
}
