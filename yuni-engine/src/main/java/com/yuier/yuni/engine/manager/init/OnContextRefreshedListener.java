package com.yuier.yuni.engine.manager.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Title: OnContextRefreshedListener
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/22 6:28
 * @description:
 */

@Component
public class OnContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    AutoPolymorphicScanner scanner;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
//            scanner.scanAndRegisterPolymorphicTypes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
