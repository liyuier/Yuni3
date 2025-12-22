package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: ShakeData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:02
 * @description: 窗口抖动消息段（发） data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class ShakeData {

    @Override
    public String toString() {
        return "[窗口抖动消息]";
    }
}
