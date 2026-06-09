package com.yuier.yuni.core.api.message;

/**
 * @Title: SendMessage
 * @Author yuier
 * @Package com.yuier.yuni.core.api.message
 * @Date 2025/12/26 3:56
 * @description:
 */

public abstract class SendMessage {

    public abstract Long getMessageId();

    /**
     * 创建一个占位实例，供向后兼容使用
     */
    public static SendMessage dummy() {
        return new SendMessage() {
            @Override
            public Long getMessageId() {
                return 0L;
            }
        };
    }
}
