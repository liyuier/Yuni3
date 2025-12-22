package com.yuier.yuni.core.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: OneBotEvent
 * @Author yuier
 * @Package com.yuier.yuni.core.model.event
 * @Date 2025/12/22 4:52
 * @description: OneBot 上报事件抽象类
 */

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)  // 指定为 protected
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "post_type",
        visible = true
)
public class OneBotEvent {

    // 事件发生的时间戳
    private Long time;

    // 收到消息的机器人 QQ 号
    private Long selfId;

    /**
     * 事件类型。此处用于标注子类
     * message：消息事件
     * notice：通知事件
     * request：请求事件
     * meta_event：元事件
     */
    @JsonProperty("post_type")
    private String postType;
}
