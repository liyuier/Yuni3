package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.core.model.event.model.GroupUploadFile;
import com.yuier.yuni.event.util.EventLogUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: GroupUploadEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/30 0:17
 * @description: 群文件上传事件
 */

@Data
@NoArgsConstructor
public class GroupUploadEvent extends YuniNoticeEvent {

    private String subType;
    private Long userId;
    private GroupUploadFile file;
    private Long groupId;

    @Override
    public String toLogString() {
        return "群文件上传事件: " + EventLogUtil.memberAtGroupLogString(groupId, userId) +
                "上传了文件: " + file.getName() + "(" + file.getId() + ")";
    }
}
