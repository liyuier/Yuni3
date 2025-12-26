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
        use = JsonTypeInfo.Id.NAME,  // 指定类型标识的生成方式为类的简短名称
        include = JsonTypeInfo.As.EXISTING_PROPERTY,  // 类型信息已经存在于 json 中，Jackson 无需额外添加
        property = "post_type",  // 存储类型信息的关键字段
        visible = true  // 反序列化后的结果显示关键字段，即此处的 postType 字段
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

    private String rawJson;
}
