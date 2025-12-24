package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.core.util.PolymorphicRegistrationProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Title: PolymorphicInitializationR
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/22 7:08
 * @description:
 */

@Component
public class PolymorphicInitializationRunner implements ApplicationRunner {

    private final PolymorphicRegistrationProcessor registrationProcessor;

    public PolymorphicInitializationRunner(PolymorphicRegistrationProcessor registrationProcessor) {
        this.registrationProcessor = registrationProcessor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 扫描注册父子类，供 jackson 反序列化使用
        registrationProcessor.initializeIfNeeded();
    }
}
