package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Title: AnonymousData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:03
 * @description: 匿名消息段（发）data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class AnonymousData {
    // 可选，表示无法匿名时是否继续发送
    private String ignore;

    @Override
    public String toString() {
        return "[匿名消息]";
    }
}
