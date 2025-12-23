package com.yuier.yuni.core.model.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: MessageSegment
 * @Author yuier
 * @Package com.yuier.yuni.core.model.message
 * @Date 2025/12/22 4:50
 * @description: 消息段的抽象类
 */

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)  // 指定为 protected
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
public abstract class MessageSegment {

    protected String type;

    protected MessageSegment(String type) {
        this.type = type;
    }

    public Boolean typeOf(String type) {
        return this.type.equals(type);
    }



}
