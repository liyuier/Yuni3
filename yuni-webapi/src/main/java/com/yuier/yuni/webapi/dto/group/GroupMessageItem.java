package com.yuier.yuni.webapi.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 群聊天记录条目。
 */
@Data
@AllArgsConstructor
public class GroupMessageItem {
    /** 发送者名称 */
    private String senderName;
    /** 消息文本（raw_message 回退，纯文本时直接可用） */
    private String message;
    /** 格式化时间 */
    private String time;
    /** 是否为 Bot 自身发送 */
    private boolean selfSent;
    /** 是否为纯文本消息 */
    private boolean plainText;
    /** 消息段数组，每个元素含 type 和 data，如 [{"type":"text","data":{"text":"hello"}}] */
    private List<Map<String, Object>> segments;
}
