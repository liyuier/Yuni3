package com.yuier.yuni.engine.manager.context;

import com.yuier.yuni.event.context.ChatSession;
import org.springframework.stereotype.Component;

/**
 * @Title: RequestContextManager
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.context
 * @Date 2025/12/22 18:35
 * @description: 请求上下文管理器
 */

@Component
public class RequestContextManager {

    // 将上下文容器存入 ThreadLocal 中
    private static final ThreadLocal<RequestContextContainer> contextHolder = new ThreadLocal<>();

    public static void setContext(RequestContextContainer container) {
        contextHolder.set(container);
    }

    public static RequestContextContainer getContext() {
        return contextHolder.get();
    }

    public static ChatSession getChatSession() {
        RequestContextContainer container = getContext();
        return container != null ? container.getChatSession() : null;
    }

    public static void clear() {
        contextHolder.remove();
    }
}
