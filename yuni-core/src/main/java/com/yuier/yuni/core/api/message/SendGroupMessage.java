package com.yuier.yuni.core.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: SendGroupMessage
 * @Author yuier
 * @Package com.yuier.yuni.core.api.message
 * @Date 2025/12/26 3:42
 * @description: 发送群组消息响应
 */

@Data
@NoArgsConstructor
public class SendGroupMessage extends SendMessage {

    @JsonProperty("message_id")
    private Long messageId;
}
