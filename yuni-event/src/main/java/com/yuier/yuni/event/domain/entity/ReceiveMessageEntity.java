package com.yuier.yuni.event.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (ReceiveMessage)表实体类
 *
 * @author liyuier
 * @since 2025-12-27 01:26:22
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("receive_message")
public class ReceiveMessageEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 时间戳
     */
    private Long timeStamp;

    /**
     * 格式化时间
     */
    private String formatTime;

    /**
     * 机器人 ID
     */
    private Long selfId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息子类型
     */
    private String subType;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者 ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 发送者群聊角色
     */
    private String role;

    /**
     * 日志化字符串
     */
    private String toLogStr;

    /**
     * 请求自带的字段，OneBot 中为 CQ 码
     */
    private String rawMessage;

    /**
     * 原始请求 json
     */
    private String rawJson;

    /**
     * 群聊 ID
     */
    private Long groupId;

    /**
     * 消息类型，是数组还是 CQ 码
     */
    private String messageFormat;

    /**
     * 真实 ID 就是最真实的 ID （划掉）
     * 其实这个字段在协议的 get_msg() 接口上会响应出来
     */
    private Long realId;

    /**
     * 是否纯文本
     */
    private Boolean isPlainText;

    /**
     * 是否是机器人发送的
     */
    private Boolean isSelfSent;

    /**
     * 是否是命令，true 为真，否则为 null
     */
    private Boolean isCommand;
}
