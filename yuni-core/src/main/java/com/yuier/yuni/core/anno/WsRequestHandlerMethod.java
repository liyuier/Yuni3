package com.yuier.yuni.core.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: WsRequestHandlerMethod
 * @Author yuier
 * @Package com.yuier.yuni.core.anno
 * @Date 2025/12/25 19:22
 * @description: ws api 注册注解
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WsRequestHandlerMethod {
    /**
     * 消息类型
     */
    String value();

    /**
     * 消息子类型（可选）
     */
    String subType() default "";
}
