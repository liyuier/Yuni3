package com.yuier.yuni.event.context.meta;

import com.yuier.yuni.core.model.event.MetaEvent;
import com.yuier.yuni.event.context.SpringYuniEvent;
import lombok.Data;

/**
 * @Title: YuniMetaEvent
 * @Author yuier
 * @Package com.yuier.yuni.event.context.meta
 * @Date 2025/12/29 19:14
 * @description: 元事件
 */

@Data
public abstract class YuniMetaEvent extends SpringYuniEvent {

    private MetaEvent rawMetaEvent;

    private String metaEventType;

    private String subType;

}
