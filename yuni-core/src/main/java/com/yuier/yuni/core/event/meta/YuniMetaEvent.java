package com.yuier.yuni.core.event.meta;

import com.yuier.yuni.core.event.YuniEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: YuniMetaEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.meta
 * @Date 2025/12/29 19:14
 * @description: 元事件
 */

@Data
@NoArgsConstructor
public class YuniMetaEvent extends YuniEvent {

    private String metaEventType;

    private String subType;

    /** 匹配到的具体元事件子类型（由 detector 设置） */
    private YuniMetaEvent matchedEvent;

    @Override
    public String toLogString() {
        return "收到未定义元事件。";
    }
}
