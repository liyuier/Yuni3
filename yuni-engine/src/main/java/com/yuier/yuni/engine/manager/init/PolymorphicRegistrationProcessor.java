package com.yuier.yuni.engine.manager.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class PolymorphicRegistrationProcessor {

    // TODO 理顺逻辑

    private final Map<Class<?>, Set<Class<?>>> polymorphicMappings = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    public PolymorphicRegistrationProcessor() {
        // 预注册基类
        registerPolymorphicBaseClass(OneBotEvent.class);
        registerPolymorphicBaseClass(MessageSegment.class);
    }

    public void registerPolymorphicBaseClass(Class<?> baseClass) {
        polymorphicMappings.putIfAbsent(baseClass, new HashSet<>());
    }

    public void registerSubType(Class<?> baseClass, Class<?> subType) {
        polymorphicMappings.computeIfAbsent(baseClass, k -> new HashSet<>())
                .add(subType);
    }

    // 🔥 修正：这是一个方法，不是成员变量
    public synchronized void initializeIfNeeded() {
        if (initialized) return;

        try {
            performScan();
            initialized = true;
            log.info("Polymorphic types registered.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize polymorphic types", e);
        }
    }

    private void performScan() throws Exception {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(PolymorphicSubType.class));

        String packageToScan = "com.yuier.yuni.core";
        java.util.Set<org.springframework.beans.factory.config.BeanDefinition> candidates =
                scanner.findCandidateComponents(packageToScan);


        for (org.springframework.beans.factory.config.BeanDefinition candidate : candidates) {
            String className = candidate.getBeanClassName();

            try {
                Class<?> clazz = Class.forName(className);

                PolymorphicSubType annotation = clazz.getAnnotation(PolymorphicSubType.class);
                String typeName = annotation.value();
                if (typeName == null || typeName.trim().isEmpty()) {
                    typeName = inferTypeName(clazz);
                }

                Class<?> baseClass = findPolymorphicBaseClass(clazz);
                if (baseClass != null) {
                    registerSubType(baseClass, clazz);
                }
            } catch (Exception e) {
                System.err.println("处理类失败: " + className);
                e.printStackTrace();
            }
        }
    }

    public void applyTo(ObjectMapper mapper) {
        initializeIfNeeded(); // 🔥 确保在应用前完成初始化

        for (Map.Entry<Class<?>, Set<Class<?>>> entry : polymorphicMappings.entrySet()) {
            Class<?> baseClass = entry.getKey();
            Set<Class<?>> subTypes = entry.getValue();


            List<NamedType> namedTypes = subTypes.stream()
                    .map(clazz -> {
                        String typeName = inferTypeName(clazz);
                        return new NamedType(clazz, typeName);
                    })
                    .toList();

            if (!namedTypes.isEmpty()) {
                mapper.registerSubtypes(namedTypes.toArray(new NamedType[0]));
            }
        }
    }

    private String inferTypeName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName.endsWith("Event")) {
            return lowerFirst(simpleName.substring(0, simpleName.length() - 5));
        } else if (simpleName.endsWith("Segment")) {
            return lowerFirst(simpleName.substring(0, simpleName.length() - 7));
        }
        return lowerFirst(simpleName);
    }

    private String lowerFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    private Class<?> findPolymorphicBaseClass(Class<?> subType) {
        Class<?> current = subType.getSuperclass();
        while (current != null && current != Object.class) {
            if (current.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonTypeInfo.class)) {
                return current;
            }
            current = current.getSuperclass();
        }
        return null;
    }

    public Set<Class<?>> getSubTypes(Class<?> baseClass) {
        return polymorphicMappings.getOrDefault(baseClass, Collections.emptySet());
    }
}