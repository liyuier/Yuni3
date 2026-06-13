package com.yuier.yuni.webapi.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 群聊天记录响应。
 */
@Data
@AllArgsConstructor
public class GroupMessagesResp {
    private List<GroupMessageItem> messages;
    private long total;
}
