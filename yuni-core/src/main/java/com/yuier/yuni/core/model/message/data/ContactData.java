package com.yuier.yuni.core.model.message.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @Title: ContactData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:11
 * @description: 推荐好友/群消息段 data 数据
 */

@Getter
@Setter
@NoArgsConstructor
public class ContactData {
    // 推荐好友，可选值： qq/group
    private String type;
    // 被推荐人/群的 QQ 号
    private String id;

    @Override
    public String toString() {
        return "[推荐" + (type.equals("qq") ? "QQ 用户" : "QQ 群 ") + "<" + this.id + ">]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactData that = (ContactData) o;
        return Objects.equals(type, that.type) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
