package com.yuier.yuni.core.model.message;

import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: MessageChain
 * @Author yuier
 * @Package com.yuier.yuni.core.model.message.chain
 * @Date 2025/12/22 17:23
 * @description: 消息链
 */

@Getter
@Setter
@AllArgsConstructor
public class MessageChain {

    private List<MessageSegment> content;

    public MessageChain() {
        content = new ArrayList<>();
    }

}
