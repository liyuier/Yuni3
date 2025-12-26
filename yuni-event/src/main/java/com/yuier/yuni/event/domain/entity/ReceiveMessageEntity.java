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

    private Long timeStamp;

    private String formatTime;

    private Long selfId;

    private String messageType;

    private String subType;

    private Long messageId;

    private Long senderId;

    private String senderName;

    private String role;

    private String toLogStr;

    private String rawMessage;

    private String rawJson;

    private Long groupId;

    private String messageFormat;

    private Long realId;
}
