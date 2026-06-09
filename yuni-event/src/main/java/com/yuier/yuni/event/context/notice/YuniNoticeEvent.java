package com.yuier.yuni.event.context.notice;

import com.yuier.yuni.event.context.SpringYuniEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: YuniNoticeEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.notice
 * @Date 2025/12/29 19:14
 * @description: 通知事件基类
 */

@Data
@NoArgsConstructor
public class YuniNoticeEvent extends SpringYuniEvent {

    private String noticeType;

    /** 涉及的 QQ 号 */
    private Long userId;
    /** 涉及的群号 */
    private Long groupId;
    /** 操作者 QQ 号 */
    private Long operatorId;

    /** 匹配到的具体通知事件子类型（由 detector 设置） */
    private YuniNoticeEvent matchedEvent;

    @Override
    public String toLogString() {
        return "收到未定义通知事件。";
    }

    public boolean isPrivate() {
        return false;
    }
}
