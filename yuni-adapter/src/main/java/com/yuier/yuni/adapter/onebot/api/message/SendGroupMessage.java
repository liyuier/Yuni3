package com.yuier.yuni.adapter.onebot.api.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendGroupMessage extends SendMessage {

    @JsonProperty("message_id")
    private Long messageId;
}
