package com.yuier.yuni.engine.manager.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.yuier.yuni.core.anno.PolymorphicSubType;
import com.yuier.yuni.core.model.event.OneBotEvent;
import com.yuier.yuni.core.model.message.MessageSegment;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PolymorphicRegistrationProcessor {

    private final Map<Class<?>, Set<Class<?>>> polymorphicMappings = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    public PolymorphicRegistrationProcessor() {
        // 预注册基类
        registerPolymorphicBaseClass(OneBotEvent.class);
        registerPolymorphicBaseClass(MessageSegment.class);
    }

    public void registerPolymorphicBaseClass(Class<?> baseClass) {
        polymorphicMappings.putIfAbsent(baseClass, new HashSet<>());
        System.out.println("预注册基类: " + baseClass.getSimpleName());
    }

    public void registerSubType(Class<?> baseClass, Class<?> subType) {
        polymorphicMappings.computeIfAbsent(baseClass, k -> new HashSet<>())
                .add(subType);
        System.out.println("添加子类型: " + baseClass.getSimpleName() + " <- " + subType.getSimpleName());
    }

    // 🔥 修正：这是一个方法，不是成员变量
    public synchronized void initializeIfNeeded() {
        if (initialized) return;

        System.out.println("=== 开始初始化多态类型扫描 ===");
        try {
            performScan();
            initialized = true;
            System.out.println("=== 多态类型扫描完成 ===");
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

        System.out.println("在包 " + packageToScan + " 中找到 " + candidates.size() + " 个 @PolymorphicSubType 标记的类");

        for (org.springframework.beans.factory.config.BeanDefinition candidate : candidates) {
            String className = candidate.getBeanClassName();
            System.out.println("发现类: " + className);

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
                    System.out.println("注册类型: " + baseClass.getSimpleName() + " <- " +
                            clazz.getSimpleName() + " (" + typeName + ")");
                }
            } catch (Exception e) {
                System.err.println("处理类失败: " + className);
                e.printStackTrace();
            }
        }
    }

    public void applyTo(ObjectMapper mapper) {
        initializeIfNeeded(); // 🔥 确保在应用前完成初始化

        System.out.println("开始应用多态类型注册到 ObjectMapper...");
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : polymorphicMappings.entrySet()) {
            Class<?> baseClass = entry.getKey();
            Set<Class<?>> subTypes = entry.getValue();

            System.out.println("处理基类: " + baseClass.getSimpleName() + ", 子类型数量: " + subTypes.size());

            List<NamedType> namedTypes = subTypes.stream()
                    .map(clazz -> {
                        String typeName = inferTypeName(clazz);
                        System.out.println("  - 注册: " + clazz.getSimpleName() + " -> " + typeName);
                        return new NamedType(clazz, typeName);
                    })
                    .toList();

            if (!namedTypes.isEmpty()) {
                mapper.registerSubtypes(namedTypes.toArray(new NamedType[0]));
                System.out.println("  已注册 " + namedTypes.size() + " 个子类型");
            }
        }
        System.out.println("多态类型注册完成");
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