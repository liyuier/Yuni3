package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.core.model.event.NoticeEvent;
import com.yuier.yuni.event.context.SpringYuniEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: DefaultYuniNoticeDetector
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/29 19:14
 * @description: 通知事件
 */

@Data
@NoArgsConstructor
public class YuniNoticeEvent extends SpringYuniEvent {

    private NoticeEvent rawNoticeEvent;
    private YuniNoticeEvent yuniNoticeEvent;

    private String noticeType;

    @Override
    public String toLogString() {
        return "收到未定义通知事件。";
    }
}
