package com.yuier.yuni.engine.manager.context;

import com.yuier.yuni.engine.event.message.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: RequestContextContainer
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.context
 * @Date 2025/12/22 18:21
 * @description: 上下文容器
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestContextContainer {

    // 如果上报了一条消息事件，将会创建一个 chatSession
    private ChatSession chatSession;
}
