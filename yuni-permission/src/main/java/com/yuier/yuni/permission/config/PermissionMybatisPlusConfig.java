package com.yuier.yuni.permission.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Title: PermissionMybatisPlusConfig
 * @Author yuier
 * @Package com.yuier.yuni.core.config
 * @Date 2025/12/24 17:01
 * @description:
 */

@SpringBootConfiguration
@MapperScan("com.yuier.yuni.permission.mapper")
public class PermissionMybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
