package com.yuier.yuni.application;

import com.yuier.yuni.core.config.YuniAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(YuniAutoConfiguration.class)
public class YuniApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuniApplication.class, args);
    }

}
