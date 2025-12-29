package com.yuier.yuni.event.context.meta;

import com.yuier.yuni.core.model.event.MetaEvent;
import com.yuier.yuni.event.context.SpringYuniEvent;
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
public class YuniMetaEvent extends SpringYuniEvent {

    private MetaEvent rawMetaEvent;
    private YuniMetaEvent yuniMetaEvent;

    private String metaEventType;

    private String subType;

    @Override
    public String toLogString() {
        return "收到未定义元事件。";
    }
}
