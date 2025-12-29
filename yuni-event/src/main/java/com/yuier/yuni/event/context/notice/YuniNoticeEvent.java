package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.core.model.event.NoticeEvent;
import com.yuier.yuni.event.context.SpringYuniEvent;
import lombok.Data;

/**
 * @Title: YuniNoticeEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/29 19:14
 * @description: 通知事件
 */

@Data
public abstract class YuniNoticeEvent extends SpringYuniEvent {

    private NoticeEvent rawNoticeEvent;

    private String noticeType;
}
