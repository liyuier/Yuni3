package com.yuier.yuni.webapi.dto.group;

import lombok.Data;

/**
 * 群聊天记录查询请求。
 */
@Data
public class GroupMessagesReq {
    private Long groupId;
    private int page = 1;
    private int size = 50;
}
