package com.yuier.yuni.webapi.filter;

import com.yuier.yuni.adapter.qq.OneBotAdapter;
import com.yuier.yuni.engine.event.message.ChatSession;
import com.yuier.yuni.engine.manager.context.RequestContextContainer;
import com.yuier.yuni.engine.manager.context.RequestContextManager;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Title: BusinessContextFilter
 * @Author yuier
 * @Package com.yuier.yuni.engine.filter
 * @Date 2025/12/22 18:38
 * @description: 辅助业务事件的过滤器
 */

@Component
@Order(1)
@Slf4j
public class BusinessContextFilter implements Filter {

    @Autowired
    OneBotAdapter adapter;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // 为当前请求创建业务上下文容器
            RequestContextContainer requestContextContainer = new RequestContextContainer();

            // 创建 chatSession 对象并放入容器中
            requestContextContainer.setChatSession(new ChatSession(adapter));

            // 将容器放入 ThreadLocal
            RequestContextManager.setContext(requestContextContainer);

            // 继续执行请求连
            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            // 请求结束后清理上下文，防止内存泄露
            RequestContextManager.clear();
        }
    }
}
