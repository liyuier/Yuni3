package com.yuier.yuni.core.model.message.data;

import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @Title: NodeData
 * @Author yuier
 * @Package com.yuier.yuni.common.domain.message.data
 * @Date 2024/4/14 22:29
 * @description: 合并转发消息段（发）data 类
 */

@Getter
@Setter
@NoArgsConstructor
public class NodeData {
    // 转发的消息 ID
    private String id;

    /* llob 自行适配 */
    private List<MessageSegment> content;

    // 用户 ID（OneBot11 格式）
    private Long userId;

    // 昵称（OneBot11 格式）
    private String nickname;

    // 名称（go-cqhttp 格式）
    private String name;

    // UIN（go-cqhttp 格式）
    private String uin;

    @Override
    public String toString() {
        return "[合并转发消息]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return Objects.equals(id, nodeData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
