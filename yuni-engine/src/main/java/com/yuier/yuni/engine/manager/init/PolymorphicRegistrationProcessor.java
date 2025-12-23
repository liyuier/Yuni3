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

    // 存储基类与其所有子类的映射关系
    private final Map<Class<?>, Set<Class<?>>> polymorphicMappings = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> typeNameMap = new ConcurrentHashMap<>();

    private volatile boolean initialized = false;

    public PolymorphicRegistrationProcessor() {
        // 预注册基类
        registerPolymorphicBaseClass(OneBotEvent.class);
        registerPolymorphicBaseClass(MessageSegment.class);
    }

    /**
     * 注册基类
     * @param baseClass
     */
    public void registerPolymorphicBaseClass(Class<?> baseClass) {
        polymorphicMappings.putIfAbsent(baseClass, new HashSet<>());
    }

    /**
     * 注册子类到指定基类
     * @param baseClass 基类
     * @param subType 子类
     */
    public void registerSubType(Class<?> baseClass, Class<?> subType) {
        // 确保基类在映射表中存在，然后添加子类
        polymorphicMappings.computeIfAbsent(baseClass, k -> new HashSet<>())
                .add(subType);
    }

    /**
     * 初始化
     */
    public void initializeIfNeeded() {
        if (initialized) return;

        try {
            performScan();
            initialized = true;
            log.info("Polymorphic types registered.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize polymorphic types", e);
        }
    }

    /**
     * 核心：执行包扫描并注册多态类型到缓存中
     * 1. 使用 Spring 扫描器查找所有带 @PolymorphicSubType 注解的类
     * 2. 解析注解中的类型名称（或自动推断）
     * 3. 确定子类的基类（通过父类是否包含 @JsonTypeInfo）
     * 4. 将子类注册到基类的映射表中
     */
    private void performScan() throws Exception {
        // 很难想象这个类的命名人的心理状态
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // 规定扫描对象为携带了 @PolymorphicSubType 注解的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(PolymorphicSubType.class));

        // 扫描路径
        String packageToScan = "com.yuier.yuni.core";
        // 执行扫描
        java.util.Set<org.springframework.beans.factory.config.BeanDefinition> candidates =
                scanner.findCandidateComponents(packageToScan);

        // 遍历所有候选类
        for (org.springframework.beans.factory.config.BeanDefinition candidate : candidates) {
            String className = candidate.getBeanClassName();

            try {
                Class<?> clazz = Class.forName(className);
                PolymorphicSubType annotation = clazz.getAnnotation(PolymorphicSubType.class);

                // 从注解获取子类的类型名称。在本应用案例中，这个名称会在父类的关键字段的值中体现
                String typeName = annotation.value();
                if (typeName == null || typeName.trim().isEmpty()) {
                    typeName = inferTypeName(clazz);  // 如果子类注解中没有提供本子类的名称，则使用自动推断
                }
                typeNameMap.put(clazz, typeName);

                // 查找子类打了 @JsonTypeInfo 注解的基类
                Class<?> baseClass = findPolymorphicBaseClass(clazz);
                if (baseClass != null) {
                    // 注册到缓存中
                    registerSubType(baseClass, clazz);
                }
            } catch (Exception e) {
                System.err.println("处理类失败: " + className);
                e.printStackTrace();
            }
        }
    }

    /**
     * 提供给外部的接口，供应用启动时注册 ObjectMapper 使用
     * @param mapper ObjectMapper
     */
    public void applyTo(ObjectMapper mapper) {
        initializeIfNeeded(); // 确保完成初始化

        // 遍历所有缓存中的映射
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : polymorphicMappings.entrySet()) {
            Set<Class<?>> subTypes = entry.getValue();

            // 为每个子类生成 NamedType
            List<NamedType> namedTypes = subTypes.stream()
                    .map(clazz -> new NamedType(clazz, typeNameMap.getOrDefault(clazz, inferTypeName(clazz))))
                    .toList();

            if (!namedTypes.isEmpty()) {
                mapper.registerSubtypes(namedTypes.toArray(new NamedType[0]));
            }
        }
    }

    /**
     * 自动推断类型名称（用于JSON序列化标识）
     * 规则：
     * 1. 类名以 "Event" 结尾 → 去掉 "Event" 后转小写（如 MessageEvent → message）
     * 2. 类名以 "Segment" 结尾 → 去掉 "Segment" 后转小写（如 ImageSegment → image）
     * 3. 其他情况 → 直接转小写
     * @param clazz 类
     * @return 类型名称
     */
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

    /**
     * 递归查找子类的基类
     * 逻辑：
     * 1. 从子类开始向上遍历父类链
     * 2. 遇到第一个包含 @JsonTypeInfo 注解的父类即为基类
     * @param subType 子类
     * @return 基类
     */
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