package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: RpsData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 21:55
 * @description: 猜拳魔法表情
 */

@Getter
@Setter
@NoArgsConstructor
public class RpsData {

    /* llob 自行实现 */
    // 1=石头，2=剪刀，3=布
    private String result;

    @Override
    public String toString() {
        return "[猜拳魔法表情]";
    }
}
