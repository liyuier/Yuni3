package com.yuier.yuni.core.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: PolymorphicSubType
 * @Author yuier
 * @Package com.yuier.yuni.core.anno
 * @Date 2025/12/22 4:47
 * @description: 多态子类型，用于辅助自动反序列化 OneBot 上报的 json
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PolymorphicSubType {

    /**
     * 指定在 JSON 中使用的 type 名称
     * 如果不指定，则自动推断（使用类名的小写首字母版本）
     */
    String value() default "";
}
