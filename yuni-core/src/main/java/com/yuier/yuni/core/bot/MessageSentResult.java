package com.yuier.yuni.core.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: MessageSentResult
 * @Author yuier
 * @Package com.yuier.yuni.core.bot
 * @Date 2026/06/09
 * @description: 消息发送结果
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSentResult {

    /** 消息是否发送成功 */
    private boolean success;

    /** 发送成功后平台返回的消息 ID */
    private String messageId;

    /** 错误信息（失败时填充） */
    private String errorMessage;

    public static MessageSentResult ok(String messageId) {
        return new MessageSentResult(true, messageId, null);
    }

    public static MessageSentResult fail(String errorMessage) {
        return new MessageSentResult(false, null, errorMessage);
    }
}
