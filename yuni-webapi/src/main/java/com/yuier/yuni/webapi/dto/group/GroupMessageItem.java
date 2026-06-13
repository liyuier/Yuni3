package com.yuier.yuni.webapi.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 群聊天记录条目。
 */
@Data
@AllArgsConstructor
public class GroupMessageItem {
    /** 发送者名称 */
    private String senderName;
    /** 消息文本 */
    private String message;
    /** 格式化时间 */
    private String time;
    /** 是否为 Bot 自身发送 */
    private boolean selfSent;
}
