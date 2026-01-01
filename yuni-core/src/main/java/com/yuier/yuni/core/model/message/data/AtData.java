package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: AtData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 21:53
 * @description: @ 消息段 data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class AtData {

    // 被 @ 的 QQ 号。all 表示全体成员。
    private String qq;

    // 被 @ 的 QQ 用户名
    private String name;

    public AtData(String qq) {
        this.qq = qq;
    }

    @Override
    public String toString() {
        return "[@" + this.qq + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtData atData = (AtData) o;
        return Objects.equals(qq, atData.qq) && Objects.equals(name, atData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qq, name);
    }
}
