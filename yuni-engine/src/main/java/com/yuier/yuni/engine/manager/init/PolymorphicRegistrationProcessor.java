package com.yuier.yuni.engine.manager.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageSegment;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: PolymorphicRegistrationProcessor
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/22 5:39
 * @description: Polymorphic 注解的类自动注册
 */

@Component
public class PolymorphicRegistrationProcessor implements BeanPostProcessor {

    private final Map<Class<?>, Set<PolymorphicTypeEntry>> polymorphicMappings = new ConcurrentHashMap<>();

    public PolymorphicRegistrationProcessor() {
        // 自动发现需要多态反序列化的基类
        discoverPolymorphicBaseClasses();
    }

    private void discoverPolymorphicBaseClasses() {
        // 扫描所有带有 @JsonTypeInfo 注解的类作为基类
        Set<Class<?>> baseClasses = findAnnotatedClasses(com.fasterxml.jackson.annotation.JsonTypeInfo.class);
        for (Class<?> baseClass : baseClasses) {
            polymorphicMappings.putIfAbsent(baseClass, new HashSet<>());
        }
    }

    public void registerSubType(Class<?> subType, String typeName) {
        // 自动发现 subType 的基类
        Class<?> baseClass = findPolymorphicBaseClass(subType);
        if (baseClass != null) {
            polymorphicMappings.computeIfAbsent(baseClass, k -> new HashSet<>())
                    .add(new PolymorphicTypeEntry(subType, typeName));
        }
    }

    public void applyTo(ObjectMapper mapper) {
        for (Map.Entry<Class<?>, Set<PolymorphicTypeEntry>> entry : polymorphicMappings.entrySet()) {
            Class<?> baseClass = entry.getKey();
            Set<PolymorphicTypeEntry> subTypes = entry.getValue();

            List<NamedType> namedTypes = subTypes.stream()
                    .map(typeEntry -> new NamedType(typeEntry.getSubType(), typeEntry.getTypeName()))
                    .toList();

            if (!namedTypes.isEmpty()) {
                mapper.registerSubtypes(namedTypes.toArray(new NamedType[0]));
            }
        }
    }

    private Class<?> findPolymorphicBaseClass(Class<?> subType) {
        // 从当前类开始向上查找，直到找到带有 @JsonTypeInfo 注解的类
        Class<?> current = subType.getSuperclass();
        while (current != null && current != Object.class) {
            if (current.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonTypeInfo.class)) {
                return current;
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private Set<Class<?>> findAnnotatedClasses(Class<? extends java.lang.annotation.Annotation> annotation) {
        // 这里可以通过扫描或预注册的方式来发现
        // 为了简化，我们可以硬编码已知的基类，或者通过反射扫描
        Set<Class<?>> result = new HashSet<>();
        // 添加已知的基类
        result.add(OneBotEvent.class);
        result.add(MessageSegment.class);
        // 可以通过扫描包路径来动态发现更多
        return result;
    }

    // 内部类存储类型映射信息
    private static class PolymorphicTypeEntry {
        private final Class<?> subType;
        private final String typeName;

        public PolymorphicTypeEntry(Class<?> subType, String typeName) {
            this.subType = subType;
            this.typeName = typeName;
        }

        public Class<?> getSubType() { return subType; }
        public String getTypeName() { return typeName; }
    }
}
