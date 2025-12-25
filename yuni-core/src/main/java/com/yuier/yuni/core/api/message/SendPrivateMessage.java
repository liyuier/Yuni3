package com.yuier.yuni.core.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: SendPrivateMessage
 * @Author yuier
 * @Package com.yuier.yuni.core.api.message
 * @Date 2025/12/26 3:43
 * @description: 发送私聊消息
 */

@Data
@NoArgsConstructor
public class SendPrivateMessage extends SendMessage {

    @JsonProperty("message_id")
    private Long messageId;
}
