package com.yuier.yuni.engine.manager.init;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Title: PolymorphicInitializationListener
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/22 7:08
 * @description:
 */

@Component
public class PolymorphicInitializationListener {

    private final PolymorphicRegistrationProcessor registrationProcessor;

    public PolymorphicInitializationListener(PolymorphicRegistrationProcessor registrationProcessor) {
        this.registrationProcessor = registrationProcessor;
    }

    // 🔥 正确的 @EventListener 用法
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        System.out.println("=== ContextRefreshedEvent 触发，初始化多态类型 ===");
        registrationProcessor.initializeIfNeeded();
        System.out.println("=== 多态类型初始化完成 ===");
    }
}
