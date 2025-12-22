package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: DiceData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:01
 * @description: 掷骰子消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class DiceData {

    private String result;

    @Override
    public String toString() {
        return "[投骰子][result=" + result + "]";
    }
}
